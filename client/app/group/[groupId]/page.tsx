"use client"
import { useRouter } from "next/navigation"
import { Plus, User, Clock, Settings, CalendarIcon, Users, Wallet } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Header } from "@/components/Header"

export default function GroupDetailPage({ params }: { params: { groupId: string } }) {
  const router = useRouter()

  // Mock data for the group
  const groupData = {
    id: params.groupId,
    name: "회사 동료",
    description: "월간 회식 및 경비",
    totalMembers: 8,
    manager: {
      name: "김철수",
      avatar: "/placeholder.svg?height=40&width=40",
    },
    accounts: {
      groupBalance: 2400000,
      myBalance: 300000,
      myDeposit: 100000,
    },
    nextMeeting: "2024-03-15",
    monthlyFee: 30000,
    paymentStatus: "완료",
    schedules: [
      {
        id: 1,
        date: "2024-03-15",
        time: "19:00",
        title: "3월 정기 회식",
        participants: 6,
        budgetPerPerson: 50000,
        totalBudget: 300000,
        location: "강남역 인근",
      },
      {
        id: 2,
        date: "2024-03-28",
        time: "09:00",
        title: "팀 워크샵",
        participants: 8,
        budgetPerPerson: 100000,
        totalBudget: 800000,
        location: "제주도",
      },
    ],
    isManager: true, // 현재 사용자가 총무인지 여부 (실제로는 백엔드에서 확인해야 함)
  }

  return (
    <>
      <Header title={groupData.name} showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-20">
        {/* Group Info */}
        <Card>
          <CardContent className="p-4">
            <div className="flex items-center justify-between mb-4">
              <div>
                <h2 className="font-medium text-lg">{groupData.description}</h2>
                <p className="text-sm text-gray-500">다음 모임: {groupData.nextMeeting}</p>
              </div>
              <div className="flex flex-col items-end">
                <div className="flex items-center text-sm text-gray-600">
                  <User className="h-4 w-4 mr-1" />
                  총무
                </div>
                <div className="flex items-center mt-1">
                  <Avatar className="h-6 w-6 mr-2">
                    <AvatarImage src={groupData.manager.avatar} />
                    <AvatarFallback>KM</AvatarFallback>
                  </Avatar>
                  <span className="text-sm">{groupData.manager.name}</span>
                </div>
              </div>
            </div>
            {groupData.isManager && (
              <Button
                variant="outline"
                className="w-full mt-2"
                onClick={() => router.push(`/group/${params.groupId}/members`)}
              >
                <Settings className="h-4 w-4 mr-2" />
                회원 관리
              </Button>
            )}
          </CardContent>
        </Card>

        {/* Account Info */}
        <div className="grid grid-cols-3 gap-3">
          <Card>
            <CardContent className="p-3">
              <div className="text-xs text-gray-500 mb-1">모임계좌 잔액</div>
              <div className="font-semibold text-blue-600">{groupData.accounts.groupBalance.toLocaleString()}원</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-3">
              <div className="text-xs text-gray-500 mb-1">내 계좌 잔액</div>
              <div className="font-semibold text-green-600">{groupData.accounts.myBalance.toLocaleString()}원</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-3">
              <div className="text-xs text-gray-500 mb-1">보증금</div>
              <div className="font-semibold">{groupData.accounts.myDeposit.toLocaleString()}원</div>
            </CardContent>
          </Card>
        </div>

        {/* Monthly Status */}
        <Card>
          <CardContent className="p-4">
            <div className="flex justify-between items-center mb-2">
              <div className="text-sm text-gray-600">이번 달 모임비</div>
              <Badge variant="outline" className="text-green-600">
                {groupData.paymentStatus}
              </Badge>
            </div>
            <div className="text-lg font-semibold">{groupData.monthlyFee.toLocaleString()}원</div>
          </CardContent>
        </Card>

        {/* Schedules */}
        <div className="space-y-3">
          <div className="flex justify-between items-center">
            <h2 className="text-lg font-semibold">일정</h2>
            <Button onClick={() => router.push(`/group/${params.groupId}/create-schedule`)}>
              <Plus className="h-4 w-4 mr-2" />새 일정 만들기
            </Button>
          </div>

          {groupData.schedules.map((schedule) => (
            <Card
              key={schedule.id}
              className="hover:shadow-md transition-shadow cursor-pointer"
              onClick={() => router.push(`/group/${params.groupId}/schedule/${schedule.id}`)}
            >
              <CardContent className="p-4">
                <div className="flex justify-between items-start mb-2">
                  <div className="font-medium">{schedule.title}</div>
                  <Badge variant="secondary">#{schedule.id}차</Badge>
                </div>
                <div className="grid grid-cols-2 gap-2 text-sm text-gray-600">
                  <div className="flex items-center">
                    <CalendarIcon className="h-4 w-4 mr-1" />
                    {schedule.date}
                  </div>
                  <div className="flex items-center">
                    <Users className="h-4 w-4 mr-1" />
                    {schedule.participants}명
                  </div>
                  <div className="flex items-center">
                    <Wallet className="h-4 w-4 mr-1" />
                    {schedule.budgetPerPerson.toLocaleString()}원/인
                  </div>
                  <div className="flex items-center">
                    <Clock className="h-4 w-4 mr-1" />
                    {schedule.location}
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </main>
    </>
  )
}

