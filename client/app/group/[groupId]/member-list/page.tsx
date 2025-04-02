"use client"
// 모두가 보는페이지지
import { useState, use, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Shield, Mail } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { Header } from "@/components/Header"
import { publicApi, authApi } from "@/lib/api"
import { toast } from "@/components/ui/use-toast"

interface Member {
  name: string;
  email: string;
  createdAt: string;
}

interface GroupMemberResponse {
  memberCount: number;
  manager: Member;
  members: Member[];
}

export default function GroupMemberListPage({ params }: { params: Promise<{ groupId: string }> }) {
  const router = useRouter()
  const {groupId} = use(params)
  const [isLoading, setIsLoading] = useState(true)
  const [groupData, setGroupData] = useState<GroupMemberResponse | null>(null)

  // API 호출 함수
  const fetchGroupMembers = async () => {
    try {
      setIsLoading(true)
      const response = await authApi.get<GroupMemberResponse>(`api/v1/gathering/${groupId}/members`) as unknown as GroupMemberResponse
      console.log(response)
      setGroupData(response)
    } catch (error) {
      console.error('Error fetching group members:', error)
      toast({
        title: "데이터 로딩 실패",
        description: "회원 정보를 불러오는데 실패했습니다.",
        variant: "destructive",
      })
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    fetchGroupMembers()
  }, [groupId])

  return (
    <>
      <Header title="회원 목록" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16">
        {/* Summary Card */}
        <Card>
          <CardHeader>
            <CardTitle className="flex justify-between items-center">
              <span>회원 목록</span>
              <Badge className="bg-blue-100 text-blue-800">
                {isLoading ? "로딩 중..." : `총 ${groupData?.memberCount || 0}명`}
              </Badge>
            </CardTitle>
          </CardHeader>
        </Card>

        {/* Members List */}
        <div className="space-y-3">
          {/* 총무 정보 */}
          {groupData?.manager && (
            <Card className="hover:shadow-md transition-shadow border-blue-100">
              <CardContent className="p-4">
                <div className="flex items-start justify-between">
                  <div className="flex items-center">
                    <Avatar className="h-12 w-12 bg-blue-100">
                      <AvatarFallback className="text-blue-600">{groupData.manager.name.slice(0, 2)}</AvatarFallback>
                    </Avatar>
                    <div className="ml-3">
                      <div className="flex items-center">
                        <span className="font-medium text-lg">{groupData.manager.name}</span>
                        <Badge variant="secondary" className="ml-2 bg-blue-100 text-blue-800 border-0">
                          <Shield className="h-3 w-3 mr-1" />
                          총무
                        </Badge>
                      </div>
                      <div className="text-sm text-gray-500 mt-1">가입일: {new Date(groupData.manager.createdAt).toLocaleDateString()}</div>
                      <div className="flex items-center text-xs text-gray-600 mt-2">
                        <Mail className="h-3 w-3 mr-1" />
                        {groupData.manager.email}
                      </div>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          {/* 일반 회원 목록 */}
          {groupData?.members.map((member, index) => (
            <Card key={index} className="hover:shadow-md transition-shadow">
              <CardContent className="p-4">
                <div className="flex items-start justify-between">
                  <div className="flex items-center">
                    <Avatar className="h-12 w-12">
                      <AvatarFallback>{member.name.slice(0, 2)}</AvatarFallback>
                    </Avatar>
                    <div className="ml-3">
                      <div className="flex items-center">
                        <span className="font-medium text-lg">{member.name}</span>
                      </div>
                      <div className="text-sm text-gray-500 mt-1">가입일: {new Date(member.createdAt).toLocaleDateString()}</div>
                      <div className="flex items-center text-xs text-gray-600 mt-2">
                        <Mail className="h-3 w-3 mr-1" />
                        {member.email}
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

