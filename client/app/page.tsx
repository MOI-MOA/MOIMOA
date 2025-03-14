"use client"

import { useState, useMemo, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Bell, MapPin, Clock, ChevronRight } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Calendar } from "@/components/ui/calendar"
import { Header } from "@/components/Header"
import { format } from "date-fns"

export default function HomePage() {
  const router = useRouter()
  const [selectedDate, setSelectedDate] = useState<Date | undefined>(new Date())
  const [currentMonth, setCurrentMonth] = useState<Date>(new Date())

  // 미확인 일정 데이터 (실제로는 API에서 받아와야 함)
  const unconfirmedSchedules = 3

  // 일정 데이터 (2025년 3월 데이터)
  type Schedule = {
    id: number;
    groupId: number;
    title: string;
    time: string;
    location: string;
    group: string;
    participants: number;
  };
  
  const scheduleData: Record<string, Schedule[]> = {
    "2025-03-05": [
      {
        id: 1,
        groupId: 4,
        title: "동호회 모임",
        time: "18:00",
        location: "강남역 인근 카페",
        group: "동호회",
        participants: 6,
      },
    ],
    "2025-03-10": [
      {
        id: 2,
        groupId: 3,
        title: "생일 파티",
        time: "19:00",
        location: "이태원 레스토랑",
        group: "가족 모임",
        participants: 4,
      },
    ],
    "2025-03-15": [
      {
        id: 3,
        groupId: 1,
        title: "3월 정기 회식",
        time: "19:00",
        location: "강남역 OO식당",
        group: "회사 동료",
        participants: 8,
      },
      {
        id: 4,
        groupId: 7,
        title: "영어 스터디",
        time: "14:00",
        location: "스터디 카페",
        group: "스터디 그룹",
        participants: 5,
      },
    ],
    "2025-03-20": [
      {
        id: 6,
        groupId: 1,
        title: "팀 워크샵",
        time: "09:00",
        location: "제주도",
        group: "회사 동료",
        participants: 12,
      },
    ],
    "2025-03-25": [
      {
        id: 8,
        groupId: 2,
        title: "프로젝트 미팅",
        time: "14:00",
        location: "회의실 A",
        group: "개발팀",
        participants: 6,
      },
      {
        id: 9,
        groupId: 2,
        title: "저녁 식사",
        time: "19:00",
        location: "홍대 맛집",
        group: "대학 친구들",
        participants: 6,
      },
    ],
  }

  // 선택된 날짜의 일정
  const selectedDateSchedules = useMemo(() => {
    if (!selectedDate) return []

    const dateStr = format(selectedDate, "yyyy-MM-dd")
    return scheduleData[dateStr] || []
  }, [selectedDate, scheduleData])

  // 다가오는 일정 목록 (모든 일정을 날짜순으로 정렬)
  const upcomingSchedules = useMemo(() => {
    const today = new Date()
    const allSchedules = Object.entries(scheduleData)
      .flatMap(([dateStr, schedules]) => {
        const [year, month, day] = dateStr.split("-").map(Number)
        const date = new Date(year, month - 1, day)

        return schedules.map((schedule) => ({
          ...schedule,
          dateObj: date,
          dateStr,
        }))
      })
      .filter((schedule) => schedule.dateObj >= today)
      .sort((a, b) => a.dateObj.getTime() - b.dateObj.getTime())

    return allSchedules.slice(0, 3) // 가장 가까운 3개 일정만 표시
  }, [scheduleData])

  return (
    <>
      <Header />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16">
        {/* 미확인 일정 알림 */}
        {unconfirmedSchedules > 0 && (
          <Button
            variant="outline"
            className="w-full border-blue-200 bg-blue-50 hover:bg-blue-100 text-blue-700"
            onClick={() => router.push("/uncheck-schedule")}
          >
            <Bell className="h-4 w-4 mr-2" />
            미확인 일정 {unconfirmedSchedules}개
            <ChevronRight className="h-4 w-4 ml-auto" />
          </Button>
        )}

        {/* 이번 달 일정 - 달력 */}
        <div className="space-y-2">
          <h2 className="text-lg font-semibold">이번 달 일정</h2>
          <Card>
            <CardContent className="p-3">
              <Calendar
                mode="single"
                selected={selectedDate}
                onSelect={setSelectedDate}
                className="rounded-md border"
                schedules={scheduleData}
                month={currentMonth}
                onMonthChange={setCurrentMonth}
                fromMonth={new Date(2025, 0)} // 2025년 1월부터 선택 가능
                toMonth={new Date(2025, 11)} // 2025년 12월까지 선택 가능
              />
            </CardContent>
          </Card>
        </div>

        {/* 선택된 날짜의 일정 목록 */}
        {selectedDate && (
          <div className="space-y-2">
            <h2 className="text-lg font-semibold">{format(selectedDate, "yyyy년 MM월 dd일")} 일정</h2>
            {selectedDateSchedules.length > 0 ? (
              <div className="space-y-3">
                {selectedDateSchedules.map((schedule) => (
                  <Card
                    key={schedule.id}
                    className="hover:shadow-md transition-shadow cursor-pointer"
                    onClick={() => router.push(`/group/${schedule.groupId}/schedule/${schedule.id}`)}
                  >
                    <CardContent className="p-4">
                      <div className="flex justify-between items-start">
                        <div className="space-y-2">
                          <div className="font-medium text-lg">{schedule.title}</div>
                          <div className="text-sm text-gray-500">{schedule.group}</div>
                          <div className="flex items-center text-sm text-gray-600 space-x-4">
                            <div className="flex items-center">
                              <Clock className="h-4 w-4 mr-1" />
                              {schedule.time}
                            </div>
                            <div className="flex items-center">
                              <MapPin className="h-4 w-4 mr-1" />
                              {schedule.location}
                            </div>
                          </div>
                        </div>
                        <Badge variant="secondary" className="bg-blue-100 text-blue-800">
                          {schedule.participants}명
                        </Badge>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            ) : (
              <Card>
                <CardContent className="p-4 text-center text-gray-500">해당 날짜에 일정이 없습니다</CardContent>
              </Card>
            )}
          </div>
        )}

        {/* 다가오는 일정 */}
        <div className="space-y-2">
          <h2 className="text-lg font-semibold">다가오는 일정</h2>
          {upcomingSchedules.length > 0 ? (
            <div className="space-y-3">
              {upcomingSchedules.map((schedule) => (
                <Card
                  key={schedule.id}
                  className="hover:shadow-md transition-shadow cursor-pointer"
                  onClick={() => router.push(`/group/${schedule.groupId}/schedule/${schedule.id}`)}
                >
                  <CardContent className="p-4">
                    <div className="flex justify-between items-start">
                      <div className="space-y-2">
                        <div className="font-medium text-lg">{schedule.title}</div>
                        <div className="text-sm text-gray-500">{schedule.group}</div>
                        <div className="flex items-center text-sm text-gray-600 space-x-4">
                          <div className="flex items-center">
                            <Clock className="h-4 w-4 mr-1" />
                            {format(schedule.dateObj, "yyyy.MM.dd")} {schedule.time}
                          </div>
                          <div className="flex items-center">
                            <MapPin className="h-4 w-4 mr-1" />
                            {schedule.location}
                          </div>
                        </div>
                      </div>
                      <Badge variant="secondary" className="bg-blue-100 text-blue-800">
                        {schedule.participants}명
                      </Badge>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="p-4 text-center text-gray-500">다가오는 일정이 없습니다</CardContent>
            </Card>
          )}
        </div>
      </main>
    </>
  )
}

