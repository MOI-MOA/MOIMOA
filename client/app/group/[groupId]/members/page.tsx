"use client"

import { useRouter } from "next/navigation"
import { Shield, AlertCircle } from "lucide-react"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Header } from "@/components/Header"

export default function GroupMembersPage({ params }: { params: { groupId: string } }) {
  const router = useRouter()

  // Mock data for members
  const members = [
    {
      id: 1,
      name: "김철수",
      role: "총무",
      avatar: "/placeholder.svg?height=40&width=40",
      balance: 300000,
      deposit: 100000,
      monthlyFee: {
        amount: 30000,
        status: "완료",
      },
      isManager: true,
    },
    {
      id: 2,
      name: "이영희",
      avatar: "/placeholder.svg?height=40&width=40",
      balance: 250000,
      deposit: 100000,
      monthlyFee: {
        amount: 30000,
        status: "완료",
      },
    },
    {
      id: 3,
      name: "박지성",
      avatar: "/placeholder.svg?height=40&width=40",
      balance: 180000,
      deposit: 100000,
      monthlyFee: {
        amount: 30000,
        status: "미납",
      },
    },
    {
      id: 4,
      name: "최민수",
      avatar: "/placeholder.svg?height=40&width=40",
      balance: 420000,
      deposit: 100000,
      monthlyFee: {
        amount: 30000,
        status: "완료",
      },
    },
  ]

  return (
    <>
      <Header title="회원 목록" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16">
        {/* Summary Card */}
        <Card>
          <CardContent className="p-4">
            <div className="flex justify-between items-center">
              <div className="text-sm text-gray-600">이번 달 회비 납부 현황</div>
              <div className="text-sm font-medium">
                {members.filter((m) => m.monthlyFee.status === "완료").length}/{members.length}명 완료
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Members List */}
        <div className="space-y-3">
          {members.map((member) => (
            <Card key={member.id} className="hover:shadow-md transition-shadow">
              <CardContent className="p-4">
                <div className="flex items-start justify-between">
                  <div className="flex items-center">
                    <Avatar className="h-10 w-10">
                      <AvatarImage src={member.avatar} />
                      <AvatarFallback>{member.name.slice(0, 2)}</AvatarFallback>
                    </Avatar>
                    <div className="ml-3">
                      <div className="flex items-center">
                        <span className="font-medium">{member.name}</span>
                        {member.isManager && (
                          <Badge variant="secondary" className="ml-2">
                            <Shield className="h-3 w-3 mr-1" />
                            총무
                          </Badge>
                        )}
                      </div>
                      <div className="text-sm text-gray-500 mt-1">잔액: {member.balance.toLocaleString()}원</div>
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="text-sm text-gray-600">보증금: {member.deposit.toLocaleString()}원</div>
                    <div className="mt-1">
                      <Badge
                        variant={member.monthlyFee.status === "완료" ? "outline" : "destructive"}
                        className={
                          member.monthlyFee.status === "완료" ? "text-green-600" : "bg-red-100 text-red-800 border-0"
                        }
                      >
                        {member.monthlyFee.status === "미납" && <AlertCircle className="h-3 w-3 mr-1" />}
                        {member.monthlyFee.status}
                      </Badge>
                    </div>
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

