"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Shield, Mail, Phone } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Header } from "@/components/Header"

export default function GroupMemberListPage({ params }: { params: { groupId: string } }) {
  const router = useRouter()
  const groupId = params.groupId

  // 모임 정보 (실제로는 API에서 가져와야 함)
  const [groupInfo, setGroupInfo] = useState({
    name: "회사 동료",
    totalMembers: 8,
  })

  // 회원 목록 데이터 (실제로는 API에서 가져와야 함)
  const [members, setMembers] = useState([
    {
      id: 1,
      name: "김철수",
      role: "총무",
      avatar: "/placeholder.svg?height=40&width=40",
      joinDate: "2023-01-15",
      contact: {
        email: "kim@example.com",
        phone: "010-1234-5678",
      },
      isManager: true,
    },
    {
      id: 2,
      name: "이영희",
      avatar: "/placeholder.svg?height=40&width=40",
      joinDate: "2023-02-20",
      contact: {
        email: "lee@example.com",
        phone: "010-2345-6789",
      },
    },
    {
      id: 3,
      name: "박지성",
      avatar: "/placeholder.svg?height=40&width=40",
      joinDate: "2023-03-10",
      contact: {
        email: "park@example.com",
        phone: "010-3456-7890",
      },
    },
    {
      id: 4,
      name: "최민수",
      avatar: "/placeholder.svg?height=40&width=40",
      joinDate: "2023-04-05",
      contact: {
        email: "choi@example.com",
        phone: "010-4567-8901",
      },
    },
    {
      id: 5,
      name: "정다운",
      avatar: "/placeholder.svg?height=40&width=40",
      joinDate: "2023-05-15",
      contact: {
        email: "jung@example.com",
        phone: "010-5678-9012",
      },
    },
    {
      id: 6,
      name: "강하늘",
      avatar: "/placeholder.svg?height=40&width=40",
      joinDate: "2023-06-20",
      contact: {
        email: "kang@example.com",
        phone: "010-6789-0123",
      },
    },
    {
      id: 7,
      name: "윤서연",
      avatar: "/placeholder.svg?height=40&width=40",
      joinDate: "2023-07-10",
      contact: {
        email: "yoon@example.com",
        phone: "010-7890-1234",
      },
    },
    {
      id: 8,
      name: "한지민",
      avatar: "/placeholder.svg?height=40&width=40",
      joinDate: "2023-08-05",
      contact: {
        email: "han@example.com",
        phone: "010-8901-2345",
      },
    },
  ])

  return (
    <>
      <Header title={`${groupInfo.name} 회원 목록`} showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16">
        {/* Summary Card */}
        <Card>
          <CardHeader>
            <CardTitle className="flex justify-between items-center">
              <span>회원 목록</span>
              <Badge className="bg-blue-100 text-blue-800">총 {members.length}명</Badge>
            </CardTitle>
          </CardHeader>
        </Card>

        {/* Members List */}
        <div className="space-y-3">
          {members.map((member) => (
            <Card key={member.id} className="hover:shadow-md transition-shadow">
              <CardContent className="p-4">
                <div className="flex items-start justify-between">
                  <div className="flex items-center">
                    <Avatar className="h-12 w-12">
                      <AvatarImage src={member.avatar} />
                      <AvatarFallback>{member.name.slice(0, 2)}</AvatarFallback>
                    </Avatar>
                    <div className="ml-3">
                      <div className="flex items-center">
                        <span className="font-medium text-lg">{member.name}</span>
                        {member.isManager && (
                          <Badge variant="secondary" className="ml-2">
                            <Shield className="h-3 w-3 mr-1" />
                            총무
                          </Badge>
                        )}
                      </div>
                      <div className="text-sm text-gray-500 mt-1">가입일: {member.joinDate}</div>
                      <div className="flex flex-col mt-2 space-y-1">
                        <div className="flex items-center text-xs text-gray-600">
                          <Mail className="h-3 w-3 mr-1" />
                          {member.contact.email}
                        </div>
                        <div className="flex items-center text-xs text-gray-600">
                          <Phone className="h-3 w-3 mr-1" />
                          {member.contact.phone}
                        </div>
                      </div>
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

