"use client"

import { useRouter } from "next/navigation"
import { Calendar, Clock, MapPin, Users, ChevronRight } from "lucide-react"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Header } from "@/components/Header"

export default function UncheckSchedulePage() {
  const router = useRouter()

  // 미확인 일정 데이터 (실제로는 API에서 받아와야 함)
  const uncheckSchedules = [
    {
      id: 1,
      groupId: 1,
      title: "3월 정기 회식",
      date: "2024-03-15",
      time: "19:00",
      location: "강남역 OO식당",
      participants: 8,
      group: "회사 동료",
    },
    {
      id: 2,
      groupId: 2,
      title: "팀 워크샵",
      date: "2024-03-28",
      time: "09:00",
      location: "제주도",
      participants: 12,
      group: "개발팀",
    },
    {
      id: 3,
      groupId: 3,
      title: "프로젝트 킥오프 미팅",
      date: "2024-04-01",
      time: "14:00",
      location: "회의실 A",
      participants: 6,
      group: "프로젝트 팀",
    },
  ]

  return (
    <>
      <Header title="미확인 일정" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16">
        {uncheckSchedules.map((schedule) => (
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
                      <Calendar className="h-4 w-4 mr-1" />
                      {schedule.date}
                    </div>
                    <div className="flex items-center">
                      <Clock className="h-4 w-4 mr-1" />
                      {schedule.time}
                    </div>
                  </div>
                  <div className="flex items-center text-sm text-gray-600">
                    <MapPin className="h-4 w-4 mr-1" />
                    {schedule.location}
                  </div>
                </div>
                <div className="flex flex-col items-end">
                  <Badge variant="secondary" className="mb-2">
                    <Users className="h-3 w-3 mr-1" />
                    {schedule.participants}명
                  </Badge>
                  <ChevronRight className="h-5 w-5 text-gray-400" />
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </main>
    </>
  )
}

