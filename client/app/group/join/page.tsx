"use client"

import { useState, useEffect } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { Header } from "@/components/Header"
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { toast } from "@/components/ui/use-toast"
import { 
  Loader2, 
  Users, 
  UserPlus, 
  XCircle, 
  AlertCircle, 
  CheckCircle, 
  ArrowLeft, 
  Shield, 
  InfoIcon,
  MessageSquare
} from "lucide-react"
import { authApi } from "@/lib/api"

interface GroupInfo {
  name: string;
  introduction: string;
  memberCount: number;
}

interface JoinResponse {
  isSave: boolean;
}

export default function JoinGroupPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const code = searchParams.get("code")
  const groupId = searchParams.get("groupId")

  const [isLoading, setIsLoading] = useState(true)
  const [groupInfo, setGroupInfo] = useState<GroupInfo | null>(null)
  const [error, setError] = useState("")
  const [isJoining, setIsJoining] = useState(false)

  useEffect(() => {
    // 초대 코드 검증 및 그룹 정보 가져오기
    const validateInviteCode = async () => {
      if (!code || !groupId) {
        setError("유효하지 않은 초대 링크입니다.")
        setIsLoading(false)
        return
      }

      try {
        const response = await authApi.get<GroupInfo>(`api/v1/gathering/${groupId}/accept-page`) as unknown as GroupInfo
        setGroupInfo(response)
      } catch (error) {
        console.error('Error fetching group info:', error)
        setError("유효하지 않은 초대 링크이거나 만료되었습니다.")
      } finally {
        setIsLoading(false)
      }
    }

    validateInviteCode()
  }, [code, groupId])

  const handleJoinGroup = async () => {
    setIsJoining(true)

    try {
      const response = await authApi.post<JoinResponse>(`api/v1/gathering/${groupId}/join`) as unknown as JoinResponse
      
      if (response.isSave) {
        toast({
          title: "모임 참가 신청 완료",
          description: "모임 참가 신청이 완료되었습니다. 총무의 승인을 기다려주세요.",
          duration: 3000,
        })
        router.push("/group")
      } else {
        throw new Error("참가 신청에 실패했습니다.")
      }
    } catch (error) {
      console.error("모임 참가 신청 실패:", error)
      toast({
        title: "모임 참가 실패",
        description: "모임 참가 중 오류가 발생했습니다. 다시 시도해주세요.",
        variant: "destructive",
        duration: 3000,
      })
    } finally {
      setIsJoining(false)
    }
  }

  return (
    <>
      <Header title="모임 참가하기" showBackButton />
      <main className="flex-1 overflow-auto p-4 flex items-center justify-center bg-slate-50">
        <div className="w-full max-w-md space-y-4">
          <div className="text-center mb-2">
            <div className="bg-blue-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-3">
              <UserPlus className="h-8 w-8 text-blue-600" />
            </div>
            <h1 className="text-2xl font-bold text-slate-800">모임 참가하기</h1>
            <p className="text-slate-500 mt-1">초대 링크를 통해 모임에 참가합니다.</p>
          </div>
          
          <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
            <CardContent className="p-0">
              {isLoading ? (
                <div className="flex flex-col items-center justify-center py-12">
                  <Loader2 className="h-10 w-10 animate-spin text-blue-500 mb-4" />
                  <p className="text-slate-600">초대 링크 확인 중...</p>
                </div>
              ) : error ? (
                <div className="text-center py-8">
                  <div className="bg-red-100 p-3 rounded-full mx-auto w-16 h-16 flex items-center justify-center mb-4">
                    <AlertCircle className="h-8 w-8 text-red-500" />
                  </div>
                  <h3 className="font-semibold text-lg text-red-600 mb-2">오류 발생</h3>
                  <p className="text-slate-600 mb-6">{error}</p>
                  <Button 
                    variant="outline" 
                    className="rounded-xl border-slate-200 hover:bg-slate-100"
                    onClick={() => router.push("/group")}
                  >
                    <ArrowLeft className="h-4 w-4 mr-2" />
                    모임 목록으로 돌아가기
                  </Button>
                </div>
              ) : groupInfo ? (
                <div className="space-y-6 p-6">
                  <div className="bg-gradient-to-r from-blue-600 to-indigo-600 -mx-6 -mt-6 p-6 text-white mb-6">
                    <div className="flex items-center mb-2">
                      <Users className="h-5 w-5 mr-2" />
                      <h3 className="font-semibold text-lg">{groupInfo.name}</h3>
                    </div>
                    <p className="text-blue-100">{groupInfo.introduction}</p>
                    <div className="mt-3 bg-blue-500/30 rounded-full px-3 py-1 text-sm inline-flex items-center">
                      <Users className="h-3.5 w-3.5 mr-1.5" />
                      참여 인원: {groupInfo.memberCount}명
                    </div>
                  </div>
                  
                  <div className="bg-blue-50 rounded-xl p-4 border border-blue-100">
                    <div className="flex">
                      <div className="bg-blue-100 p-2 rounded-full mr-3 flex-shrink-0">
                        <InfoIcon className="h-5 w-5 text-blue-600" />
                      </div>
                      <div>
                        <h3 className="text-sm font-medium text-blue-800 mb-1">참가 안내</h3>
                        <p className="text-xs text-blue-700">
                          모임에 참가하려면 총무의 승인이 필요합니다. 참가 신청 후 승인을 기다려주세요.
                        </p>
                      </div>
                    </div>
                  </div>
                  
                  <div className="grid grid-cols-2 gap-4 mt-6">
                    <div className="bg-slate-100 rounded-xl p-4 text-center">
                      <Shield className="h-6 w-6 mx-auto mb-2 text-slate-600" />
                      <h4 className="text-sm font-medium text-slate-700">총무 승인 필요</h4>
                    </div>
                    <div className="bg-slate-100 rounded-xl p-4 text-center">
                      <MessageSquare className="h-6 w-6 mx-auto mb-2 text-slate-600" />
                      <h4 className="text-sm font-medium text-slate-700">모임 채팅 참여</h4>
                    </div>
                  </div>
                  
                  <div className="flex space-x-3 pt-2">
                    <Button 
                      variant="outline" 
                      className="flex-1 rounded-xl border-slate-200 hover:bg-slate-100 text-slate-700"
                      onClick={() => router.push("/group")}
                    >
                      <XCircle className="h-4 w-4 mr-2" />
                      취소
                    </Button>
                    <Button 
                      className="flex-1 rounded-xl bg-blue-600 hover:bg-blue-700"
                      onClick={handleJoinGroup}
                      disabled={isJoining}
                    >
                      {isJoining ? (
                        <div className="flex items-center justify-center">
                          <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                          처리 중...
                        </div>
                      ) : (
                        <div className="flex items-center justify-center">
                          <UserPlus className="h-4 w-4 mr-2" />
                          참가 신청하기
                        </div>
                      )}
                    </Button>
                  </div>
                </div>
              ) : null}
            </CardContent>
          </Card>
        </div>
      </main>
    </>
  )
}

