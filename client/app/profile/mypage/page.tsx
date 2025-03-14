"use client"

import { useRouter } from "next/navigation"
import { Header } from "@/components/Header"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Switch } from "@/components/ui/switch"
import { Edit, User, Shield, LogOut } from "lucide-react"
import { useState } from "react"
import { toast } from "@/components/ui/use-toast"

export default function MyPage() {
  const router = useRouter()

  // 사용자 정보 (실제로는 API에서 가져와야 함)
  const [userInfo, setUserInfo] = useState({
    name: "배한진",
    email: "user@example.com",
    phone: "010-1234-5678",
    avatar: "/placeholder.svg?height=100&width=100",
    notifications: {
      email: true,
      push: true,
      sms: false,
    },
  })

  const handleNotificationChange = (type: string, checked: boolean) => {
    // 상태 업데이트
    setUserInfo((prev) => ({
      ...prev,
      notifications: {
        ...prev.notifications,
        [type]: checked,
      },
    }))

    // 토스트 메시지 표시
    toast({
      title: `${type === "email" ? "이메일" : type === "push" ? "푸시" : "SMS"} 알림 ${checked ? "활성화" : "비활성화"}`,
      description: `${type === "email" ? "이메일" : type === "push" ? "푸시" : "SMS"} 알림이 ${checked ? "활성화" : "비활성화"} 되었습니다.`,
    })
  }

  return (
    <>
      <Header title="마이페이지" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16">
        <Card>
          <CardHeader>
            <CardTitle>프로필 정보</CardTitle>
            <CardDescription>개인 정보를 확인하고 관리하세요</CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="flex flex-col items-center mb-4">
              <Avatar className="h-24 w-24 mb-2">
                <AvatarImage src={userInfo.avatar} />
                <AvatarFallback>{userInfo.name.slice(0, 2)}</AvatarFallback>
              </Avatar>
            </div>

            <div className="space-y-4">
              <div className="flex justify-between items-center border-b pb-2">
                <div className="space-y-1">
                  <p className="text-sm text-gray-500">이름</p>
                  <p className="font-medium">{userInfo.name}</p>
                </div>
              </div>

              <div className="flex justify-between items-center border-b pb-2">
                <div className="space-y-1">
                  <p className="text-sm text-gray-500">이메일</p>
                  <p className="font-medium">{userInfo.email}</p>
                </div>
              </div>

              <div className="flex justify-between items-center border-b pb-2">
                <div className="space-y-1">
                  <p className="text-sm text-gray-500">전화번호</p>
                  <p className="font-medium">{userInfo.phone}</p>
                </div>
              </div>
            </div>

            <Button className="w-full" onClick={() => router.push("/profile/mypage/edit")}>
              <Edit className="h-4 w-4 mr-2" />
              프로필 수정
            </Button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>알림 설정</CardTitle>
            <CardDescription>알림 수신 방법을 설정하세요</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="font-medium">이메일 알림</p>
                <p className="text-sm text-gray-500">모임 및 일정 관련 이메일 알림</p>
              </div>
              <Switch
                checked={userInfo.notifications.email}
                onCheckedChange={(checked) => handleNotificationChange("email", checked)}
              />
            </div>

            <div className="flex items-center justify-between">
              <div>
                <p className="font-medium">푸시 알림</p>
                <p className="text-sm text-gray-500">앱 푸시 알림</p>
              </div>
              <Switch
                checked={userInfo.notifications.push}
                onCheckedChange={(checked) => handleNotificationChange("push", checked)}
              />
            </div>

            <div className="flex items-center justify-between">
              <div>
                <p className="font-medium">SMS 알림</p>
                <p className="text-sm text-gray-500">문자 메시지 알림</p>
              </div>
              <Switch
                checked={userInfo.notifications.sms}
                onCheckedChange={(checked) => handleNotificationChange("sms", checked)}
              />
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>계정 관리</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <Button variant="outline" className="w-full flex justify-start">
              <Shield className="h-4 w-4 mr-2" />
              비밀번호 변경
            </Button>
            <Button variant="outline" className="w-full flex justify-start text-red-500 hover:text-red-600">
              <LogOut className="h-4 w-4 mr-2" />
              로그아웃
            </Button>
            <Button
              variant="outline"
              className="w-full flex justify-start text-red-500 hover:text-red-600 hover:bg-red-50"
            >
              <User className="h-4 w-4 mr-2" />
              계정 삭제
            </Button>
          </CardContent>
        </Card>
      </main>
    </>
  )
}

