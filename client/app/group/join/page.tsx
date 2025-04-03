"use client"

import { useState, useEffect } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { Header } from "@/components/Header"
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { toast } from "@/components/ui/use-toast"
import { Loader2 } from "lucide-react"
import { publicApi, authApi } from "@/lib/api"

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
    setIsLoading(true)

    try {
      const response = await authApi.post<JoinResponse>(`api/v1/gathering/${groupId}/join`) as unknown as JoinResponse
      console.log(response)
      
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
      setIsLoading(false)
    }
  }

  return (
    <>
      <Header title="모임 참가하기" showBackButton />
      <main className="flex-1 overflow-auto p-4 flex items-center justify-center">
        <Card className="w-full max-w-md">
          <CardHeader>
            <CardTitle>모임 참가하기</CardTitle>
            <CardDescription>초대 링크를 통해 모임에 참가합니다.</CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="flex flex-col items-center justify-center py-8">
                <Loader2 className="h-8 w-8 animate-spin text-blue-500" />
                <p className="mt-4 text-gray-500">초대 링크 확인 중...</p>
              </div>
            ) : error ? (
              <div className="text-center py-8">
                <p className="text-red-500">{error}</p>
                <Button variant="outline" className="mt-4" onClick={() => router.push("/group")}>
                  모임 목록으로 돌아가기
                </Button>
              </div>
            ) : groupInfo ? (
              <div className="space-y-4">
                <div className="p-4 bg-blue-50 rounded-lg">
                  <h3 className="font-semibold text-lg">{groupInfo.name}</h3>
                  <p className="text-gray-600">{groupInfo.introduction}</p>
                  <p className="text-sm text-gray-500 mt-2">참여 인원: {groupInfo.memberCount}명</p>
                </div>
                <p className="text-sm text-gray-500">
                  이 모임에 참가하시겠습니까? 참가 신청 후 총무의 승인이 필요합니다.
                </p>
              </div>
            ) : null}
          </CardContent>
          {!isLoading && !error && groupInfo && (
            <CardFooter>
              <div className="flex space-x-2 w-full">
                <Button variant="outline" className="flex-1" onClick={() => router.push("/group")}>
                  취소
                </Button>
                <Button className="flex-1" onClick={handleJoinGroup}>
                  참가 신청하기
                </Button>
              </div>
            </CardFooter>
          )}
        </Card>
      </main>
    </>
  )
}

