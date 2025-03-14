"use client"
import React, { use } from "react"
import { useState } from "react"
import { useRouter } from "next/navigation"
import { Calendar, Clock, MapPin, Users, DollarSign, MessageSquare } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import { Header } from "@/components/Header"

export default function ScheduleDetailPage({ params }: { params: Promise<{ groupId: string; scheduleId: string }> }) {
  const router = useRouter()
  const [isAttending, setIsAttending] = useState(false)
  const { groupId, scheduleId } = use(params)

  // 실제 구현에서는 이 데이터를 API에서 가져와야 합니다
  const scheduleData = {
    id: scheduleId,
    groupId: groupId, // 그룹 ID 파라미터 사용
    title: "3월 정기 회식",
    date: "2024년 3월 15일",
    time: "19:00 - 22:00",
    location: "강남역 OO식당",
    participants: [
      { id: "1", name: "김철수", avatar: "/placeholder.svg?height=32&width=32" },
      { id: "2", name: "이영희", avatar: "/placeholder.svg?height=32&width=32" },
      { id: "3", name: "박지성", avatar: "/placeholder.svg?height=32&width=32" },
    ],
    totalParticipants: 8,
    budget: 400000,
    description: "3월 정기 회식입니다. 모든 팀원들의 참석 바랍니다.",
    organizer: "김철수",
  }

  const handleAttendance = () => {
    // 여기에 참석 여부를 서버에 업데이트하는 로직을 추가해야 합니다
    setIsAttending(!isAttending)
  }

  return (
    <>
      <Header title="일정 상세" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16">
        <Card>
          <CardContent className="p-6 space-y-4">
            <div>
              <h2 className="text-2xl font-bold">{scheduleData.title}</h2>
              <p className="text-gray-500">{scheduleData.organizer} 주최</p>
            </div>

            <div className="space-y-2">
              <div className="flex items-center text-gray-600">
                <Calendar className="h-5 w-5 mr-2" />
                <span>{scheduleData.date}</span>
              </div>
              <div className="flex items-center text-gray-600">
                <Clock className="h-5 w-5 mr-2" />
                <span>{scheduleData.time}</span>
              </div>
              <div className="flex items-center text-gray-600">
                <MapPin className="h-5 w-5 mr-2" />
                <span>{scheduleData.location}</span>
              </div>
              <div className="flex items-center text-gray-600">
                <Users className="h-5 w-5 mr-2" />
                <span>{scheduleData.totalParticipants}명 참석 예정</span>
              </div>
              <div className="flex items-center text-gray-600">
                <DollarSign className="h-5 w-5 mr-2" />
                <span>예산: {scheduleData.budget.toLocaleString()}원</span>
              </div>
            </div>

            <div>
              <h3 className="font-semibold mb-2">설명</h3>
              <p className="text-gray-600">{scheduleData.description}</p>
            </div>

            <div>
              <h3 className="font-semibold mb-2">참석자</h3>
              <div className="flex space-x-2">
                {scheduleData.participants.map((participant) => (
                  <Avatar key={participant.id}>
                    <AvatarImage src={participant.avatar} alt={participant.name} />
                    <AvatarFallback>{participant.name[0]}</AvatarFallback>
                  </Avatar>
                ))}
                {scheduleData.totalParticipants > scheduleData.participants.length && (
                  <Badge variant="secondary">
                    +{scheduleData.totalParticipants - scheduleData.participants.length}
                  </Badge>
                )}
              </div>
            </div>

            <div className="pt-4">
              <Button className="w-full bg-blue-500 hover:bg-blue-600 text-white" onClick={handleAttendance}>
                {isAttending ? "참석 취소" : "참석하기"}
              </Button>
            </div>
          </CardContent>
        </Card>

        <Button
          variant="outline"
          className="w-full"
          onClick={() => router.push(`/group/${scheduleData.groupId}/chat/${scheduleId}`)}
        >
          <MessageSquare className="h-5 w-5 mr-2" />
          채팅방 입장
        </Button>
      </main>
    </>
  )
}

