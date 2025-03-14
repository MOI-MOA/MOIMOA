"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Header } from "@/components/Header"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { toast } from "@/components/ui/use-toast"
import { Camera } from "lucide-react"

export default function EditProfilePage() {
  const router = useRouter()

  // 사용자 정보 (실제로는 API에서 가져와야 함)
  const [userInfo, setUserInfo] = useState({
    name: "배한진",
    email: "user@example.com",
    phone: "010-1234-5678",
    avatar: "/placeholder.svg?height=100&width=100",
  })

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setUserInfo((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleSaveProfile = () => {
    // 실제로는 API를 통해 저장해야 함
    toast({
      title: "프로필 저장 완료",
      description: "프로필 정보가 성공적으로 저장되었습니다.",
    })
    router.push("/profile/mypage")
  }

  return (
    <>
      <Header title="프로필 수정" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16">
        <Card>
          <CardHeader>
            <CardTitle>프로필 정보 수정</CardTitle>
            <CardDescription>개인 정보를 업데이트하세요</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex flex-col items-center mb-4 relative">
              <Avatar className="h-24 w-24 mb-2">
                <AvatarImage src={userInfo.avatar} />
                <AvatarFallback>{userInfo.name.slice(0, 2)}</AvatarFallback>
              </Avatar>
              <Button
                variant="outline"
                size="sm"
                className="absolute bottom-0 right-1/2 translate-x-12 translate-y-1 rounded-full p-2"
              >
                <Camera className="h-4 w-4" />
              </Button>
            </div>

            <div className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="name">이름</Label>
                <Input id="name" name="name" value={userInfo.name} onChange={handleInputChange} />
              </div>

              <div className="space-y-2">
                <Label htmlFor="email">이메일</Label>
                <Input id="email" name="email" type="email" value={userInfo.email} onChange={handleInputChange} />
              </div>

              <div className="space-y-2">
                <Label htmlFor="phone">전화번호</Label>
                <Input id="phone" name="phone" value={userInfo.phone} onChange={handleInputChange} />
              </div>
            </div>

            <div className="flex space-x-2 pt-4">
              <Button variant="outline" className="flex-1" onClick={() => router.back()}>
                취소
              </Button>
              <Button className="flex-1" onClick={handleSaveProfile}>
                저장하기
              </Button>
            </div>
          </CardContent>
        </Card>
      </main>
    </>
  )
}

