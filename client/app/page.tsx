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
import axios from "axios"
import { toast } from "@/components/ui/use-toast"

type UnconfirmedSchedule = {
  scheduleTitle: string
  scheduleDetail: string
  schedulePlace: string
  scheduleTime: string
  perBudget: number
  totalBudget: number
  penaltyApplyDate: string
  smNumber: number
  smIdList: Array<{
    userId: number
    userName: string
  }>
}

type MonthlySchedule = {
  date: string
  events: Array<{
    groupId: number
    groupName: string
    scheduleId: number
    scheduleTitle: string
    scheduleDetail: string
    schedulePlace: string
    scheduleStratTime: string
    perBudget: number
    totalBudget: number
    penaltyApplyDate: string
    scheduleStatus: number
    attendeeCount: number
  }>
}

type UpcomingSchedule = {
  groupId: number
  groupName: string
  scheduleId: number
  scheduleTitle: string
  scheduleDetail: string
  schedulePlace: string
  scheduleStartTime: string
  perBudget: number
  totalBudget: number
  penaltyApplyDate: string
  scheduleStatus: number
  attendeeCount: number
}

const DEFAULT_UNCONFIRMED_SCHEDULES: UnconfirmedSchedule[] = [
  {
    scheduleTitle: "미팅",
    scheduleDetail: "팀 미팅",
    schedulePlace: "회의실",
    scheduleTime: "2025-03-10T15:00:00",
    perBudget: 20000,
    totalBudget: 100000,
    penaltyApplyDate: "2025-03-12T00:00:00",
    smNumber: 3,
    smIdList: [
      { userId: 1, userName: "홍길동" },
      { userId: 2, userName: "김철수" }
    ]
  }
];

const DEFAULT_MONTHLY_SCHEDULES: MonthlySchedule[] = [
  {
    date: "2025-03-01",
    events: [
      {
        groupId: 1,
        groupName: "첫모임",
        scheduleId: 2,
        scheduleTitle: "영어 스터디",
        scheduleDetail: "매주 영어 스터디 진행",
        schedulePlace: "스터디 카페",
        scheduleStratTime: "2025-03-15T14:00:00",
        perBudget: 15000,
        totalBudget: 75000,
        penaltyApplyDate: "2025-03-16",
        scheduleStatus: 1,
        attendeeCount: 5
      }
    ]
  },
  {
    date: "2025-03-15",
    events: [
      {
        groupId: 2,
        groupName: "영어모임",
        scheduleId: 2,
        scheduleTitle: "영어 스터디",
        scheduleDetail: "매주 영어 스터디 진행",
        schedulePlace: "스터디 카페",
        scheduleStratTime: "2025-03-15T14:00:00",
        perBudget: 15000,
        totalBudget: 75000,
        penaltyApplyDate: "2025-03-16",
        scheduleStatus: 1,
        attendeeCount: 5
      }
    ]
  }
];

const DEFAULT_UPCOMING_SCHEDULES: UpcomingSchedule[] = [
  {
    groupId: 3,
    groupName: "개발 모임",
    scheduleId: 5,
    scheduleTitle: "코딩 챌린지",
    scheduleDetail: "알고리즘 문제 풀이",
    schedulePlace: "온라인",
    scheduleStartTime: "2025-03-20T18:00:00",
    perBudget: 10000,
    totalBudget: 50000,
    penaltyApplyDate: "2025-03-21",
    scheduleStatus: 0,
    attendeeCount: 4
  },
  {
    groupId: 3,
    groupName: "개발 모임",
    scheduleId: 5,
    scheduleTitle: "코딩 챌린지",
    scheduleDetail: "알고리즘 문제 풀이",
    schedulePlace: "온라인",
    scheduleStartTime: "2025-03-20T18:00:00",
    perBudget: 10000,
    totalBudget: 50000,
    penaltyApplyDate: "2025-03-21",
    scheduleStatus: 0,
    attendeeCount: 4
  }
];

export default function HomePage() {
  const router = useRouter()
  const [selectedDate, setSelectedDate] = useState<Date | undefined>(new Date())
  const [currentMonth, setCurrentMonth] = useState<Date>(new Date())
  const [unconfirmedSchedules, setUnconfirmedSchedules] = useState<UnconfirmedSchedule[]>(DEFAULT_UNCONFIRMED_SCHEDULES)
  const [monthlySchedules, setMonthlySchedules] = useState<MonthlySchedule[]>(DEFAULT_MONTHLY_SCHEDULES)
  const [upcomingSchedules, setUpcomingSchedules] = useState<UpcomingSchedule[]>(DEFAULT_UPCOMING_SCHEDULES)
  const [isLoading, setIsLoading] = useState(true)

  // API에서 데이터 가져오기
  useEffect(() => {
    const fetchData = async () => {
      try {
        const { data } = await axios.get('/api/v1/home');
        
        setUnconfirmedSchedules(data.unconfirmedSchedules || DEFAULT_UNCONFIRMED_SCHEDULES);
        setMonthlySchedules(data.monthlySchedules || DEFAULT_MONTHLY_SCHEDULES);
        setUpcomingSchedules(data.upcomingSchedules || DEFAULT_UPCOMING_SCHEDULES);
      } catch (error) {
        console.error('일정을 가져오는데 실패했습니다:', error);
        toast({
          title: "데이터 로딩 실패",
          description: "일정 정보를 가져오는데 실패했습니다. 기본 데이터를 표시합니다.",
          variant: "destructive",
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, []);

  // 월별 일정 데이터 포맷 변환
  const scheduleData = useMemo(() => {
    const formattedData: Record<string, any[]> = {};
    
    monthlySchedules.forEach(schedule => {
      formattedData[schedule.date] = schedule.events.map(event => ({
        id: event.scheduleId,
        groupId: event.groupId,
        title: event.scheduleTitle,
        time: format(new Date(event.scheduleStratTime), 'HH:mm'),
        location: event.schedulePlace,
        group: event.groupName,
        participants: event.attendeeCount
      }));
    });

    return formattedData;
  }, [monthlySchedules]);

  // 선택된 날짜의 일정
  const selectedDateSchedules = useMemo(() => {
    if (!selectedDate) return [];
    const dateStr = format(selectedDate, "yyyy-MM-dd");
    return scheduleData[dateStr] || [];
  }, [selectedDate, scheduleData]);

  return (
    <>
      <Header />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16">
        {/* 미확인 일정 알림 */}
        {unconfirmedSchedules.length > 0 && (
          <Button
            variant="outline"
            className="w-full border-blue-200 bg-blue-50 hover:bg-blue-100 text-blue-700"
            onClick={() => router.push("/uncheck-schedule")}
          >
            <Bell className="h-4 w-4 mr-2" />
            미확인 일정 {unconfirmedSchedules.length}개
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
                fromMonth={new Date(2025, 0)}
                toMonth={new Date(2025, 11)}
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
                  key={schedule.scheduleId}
                  className="hover:shadow-md transition-shadow cursor-pointer"
                  onClick={() => router.push(`/group/${schedule.groupId}/schedule/${schedule.scheduleId}`)}
                >
                  <CardContent className="p-4">
                    <div className="flex justify-between items-start">
                      <div className="space-y-2">
                        <div className="font-medium text-lg">{schedule.scheduleTitle}</div>
                        <div className="text-sm text-gray-500">{schedule.groupName}</div>
                        <div className="flex items-center text-sm text-gray-600 space-x-4">
                          <div className="flex items-center">
                            <Clock className="h-4 w-4 mr-1" />
                            {format(new Date(schedule.scheduleStartTime), 'yyyy.MM.dd HH:mm')}
                          </div>
                          <div className="flex items-center">
                            <MapPin className="h-4 w-4 mr-1" />
                            {schedule.schedulePlace}
                          </div>
                        </div>
                      </div>
                      <Badge variant="secondary" className="bg-blue-100 text-blue-800">
                        {schedule.attendeeCount}명
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

