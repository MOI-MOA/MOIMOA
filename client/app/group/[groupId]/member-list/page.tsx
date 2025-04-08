"use client"
// 모두가 보는페이지지
import { useState, use, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Shield, Mail, Users, Crown, RefreshCw, Calendar, User } from "lucide-react"
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
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16 bg-slate-50">
        {/* 요약 카드 */}
        <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
          <CardContent className="p-5">
            <div className="flex justify-between items-center">
              <div className="flex items-center text-slate-800">
                <Users className="h-5 w-5 mr-2 text-blue-600" />
                <span className="font-medium">전체 회원</span>
              </div>
              <Badge className="bg-blue-50 text-blue-600 border-blue-200 px-3 py-1 rounded-lg">
                {isLoading ? (
                  <div className="flex items-center">
                    <RefreshCw className="h-3 w-3 mr-1 animate-spin" />
                    로딩 중...
                  </div>
                ) : (
                  `총 ${groupData?.memberCount || 0}명`
                )}
              </Badge>
            </div>
          </CardContent>
        </Card>

        {/* 회원 목록 */}
        <div className="space-y-3">
          {/* 총무 정보 */}
          {groupData?.manager && (
            <Card className="border-0 shadow-sm rounded-xl overflow-hidden bg-gradient-to-br from-blue-50 to-white hover:shadow-md transition-all duration-200 transform hover:-translate-y-0.5">
              <CardContent className="p-5">
                <div className="flex items-start justify-between">
                  <div className="flex items-center">
                    <Avatar className="h-14 w-14 border-2 border-blue-200">
                      <AvatarFallback className="bg-blue-100 text-blue-600 text-lg font-medium">
                        {groupData.manager.name.slice(0, 2)}
                      </AvatarFallback>
                    </Avatar>
                    <div className="ml-4">
                      <div className="flex items-center mb-1">
                        <span className="font-medium text-lg text-slate-800">
                          {groupData.manager.name}
                        </span>
                        <Badge className="ml-2 bg-blue-100 text-blue-700 border-0 flex items-center">
                          <Crown className="h-3 w-3 mr-1" />
                          총무
                        </Badge>
                      </div>
                      <div className="flex items-center text-sm text-slate-500 mb-1">
                        <Calendar className="h-4 w-4 mr-1.5 text-slate-400" />
                        가입일: {new Date(groupData.manager.createdAt).toLocaleDateString()}
                      </div>
                      <div className="flex items-center text-sm text-slate-500">
                        <Mail className="h-4 w-4 mr-1.5 text-slate-400" />
                        {groupData.manager.email}
                      </div>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          {/* 일반 회원 목록 */}
          <div className="grid gap-3">
            {groupData?.members.map((member, index) => (
              <Card 
                key={index} 
                className="border-0 shadow-sm rounded-xl overflow-hidden hover:shadow-md transition-all duration-200 transform hover:-translate-y-0.5"
              >
                <CardContent className="p-5">
                  <div className="flex items-start justify-between">
                    <div className="flex items-center">
                      <Avatar className="h-14 w-14 border-2 border-slate-100">
                        <AvatarFallback className="bg-slate-50 text-slate-600 text-lg font-medium">
                          {member.name.slice(0, 2)}
                        </AvatarFallback>
                      </Avatar>
                      <div className="ml-4">
                        <div className="flex items-center mb-1">
                          <span className="font-medium text-lg text-slate-800">
                            {member.name}
                          </span>
                          <Badge className="ml-2 bg-slate-100 text-slate-600 border-0 flex items-center">
                            <User className="h-3 w-3 mr-1" />
                            회원
                          </Badge>
                        </div>
                        <div className="flex items-center text-sm text-slate-500 mb-1">
                          <Calendar className="h-4 w-4 mr-1.5 text-slate-400" />
                          가입일: {new Date(member.createdAt).toLocaleDateString()}
                        </div>
                        <div className="flex items-center text-sm text-slate-500">
                          <Mail className="h-4 w-4 mr-1.5 text-slate-400" />
                          {member.email}
                        </div>
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          {/* 로딩 중이거나 데이터가 없는 경우 */}
          {isLoading && (
            <div className="flex items-center justify-center py-8">
              <div className="flex flex-col items-center gap-2">
                <RefreshCw className="h-8 w-8 text-blue-600 animate-spin" />
                <span className="text-sm text-slate-600">회원 정보를 불러오는 중...</span>
              </div>
            </div>
          )}

          {!isLoading && groupData?.members.length === 0 && (
            <div className="flex flex-col items-center justify-center py-8 text-slate-500">
              <Users className="h-12 w-12 mb-2 text-slate-300" />
              <p>아직 등록된 회원이 없습니다.</p>
            </div>
          )}
        </div>
      </main>
    </>
  )
}

