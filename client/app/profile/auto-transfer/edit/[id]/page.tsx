"use client"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Header } from "@/components/Header"
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Switch } from "@/components/ui/switch"
import { toast } from "@/components/ui/use-toast"
import { AlertCircle } from "lucide-react"

export default function EditAutoTransferPage({ params }: { params: { id: string } }) {
  const router = useRouter()
  const transferId = Number.parseInt(params.id)

  // 초기 상태 설정
  const [formData, setFormData] = useState({
    id: transferId,
    groupName: "",
    amount: "",
    day: "",
    account: "",
    status: "active",
  })

  const [isLoading, setIsLoading] = useState(false)

  // 실제 구현에서는 API에서 데이터를 가져와야 함
  // 여기서는 목업 데이터를 사용
  useEffect(() => {
    // 실제 구현에서는 API 호출로 대체
    const mockTransfers = [
      {
        id: 1,
        groupName: "회사 동료",
        amount: "30000",
        day: "15",
        account: "신한은행 110-123-456789",
        status: "active",
      },
      {
        id: 2,
        groupName: "대학 친구들",
        amount: "20000",
        day: "20",
        account: "국민은행 123-45-6789012",
        status: "active",
      },
      {
        id: 3,
        groupName: "동호회",
        amount: "15000",
        day: "5",
        account: "우리은행 1002-123-456789",
        status: "inactive",
      },
    ]

    const transfer = mockTransfers.find((t) => t.id === transferId)
    if (transfer) {
      setFormData(transfer)
    } else {
      // 데이터가 없으면 목록 페이지로 리다이렉트
      toast({
        title: "오류",
        description: "자동이체 정보를 찾을 수 없습니다.",
        variant: "destructive",
      })
      router.push("/profile/auto-transfer")
    }
  }, [transferId, router])

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleStatusChange = (checked) => {
    setFormData((prev) => ({
      ...prev,
      status: checked ? "active" : "inactive",
    }))
  }

  const handleSave = async (e) => {
    e.preventDefault()
    setIsLoading(true)

    try {
      // 실제 구현에서는 API 호출로 데이터 저장
      // await fetch(`/api/auto-transfers/${transferId}`, {
      //   method: 'PUT',
      //   headers: { 'Content-Type': 'application/json' },
      //   body: JSON.stringify(formData)
      // })

      // 저장 성공 시 토스트 메시지 표시
      setTimeout(() => {
        toast({
          title: "저장 완료",
          description: "자동이체 정보가 성공적으로 업데이트되었습니다.",
        })
        router.push("/profile/auto-transfer")
      }, 1000)
    } catch (error) {
      console.error(error)
      toast({
        title: "오류 발생",
        description: "자동이체 정보 저장 중 문제가 발생했습니다.",
        variant: "destructive",
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <>
      <Header title="자동이체 수정" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16">
        <Card>
          <CardHeader>
            <CardTitle>자동이체 정보 수정</CardTitle>
            <CardDescription>자동이체 정보를 수정하세요</CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSave} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="groupName">모임 이름</Label>
                <Input
                  id="groupName"
                  name="groupName"
                  value={formData.groupName}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="amount">금액</Label>
                <div className="relative">
                  <Input
                    id="amount"
                    name="amount"
                    type="number"
                    value={formData.amount}
                    onChange={handleInputChange}
                    required
                  />
                  <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none text-gray-500">
                    원
                  </div>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="day">매월 결제일</Label>
                <div className="flex items-center space-x-2">
                  <Input
                    id="day"
                    name="day"
                    type="number"
                    min="1"
                    max="31"
                    value={formData.day}
                    onChange={handleInputChange}
                    required
                  />
                  <div className="text-gray-500">일</div>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="account">계좌 정보</Label>
                <Input id="account" name="account" value={formData.account} onChange={handleInputChange} required />
              </div>

              <div className="flex items-center justify-between pt-2">
                <Label htmlFor="status" className="cursor-pointer">
                  자동이체 활성화
                </Label>
                <Switch id="status" checked={formData.status === "active"} onCheckedChange={handleStatusChange} />
              </div>
            </form>
          </CardContent>
          <CardFooter className="flex justify-between">
            <Button variant="outline" onClick={() => router.push("/profile/auto-transfer")} disabled={isLoading}>
              취소
            </Button>
            <Button onClick={handleSave} disabled={isLoading}>
              {isLoading ? "저장 중..." : "저장하기"}
            </Button>
          </CardFooter>
        </Card>

        {formData.status === "inactive" && (
          <div className="bg-yellow-50 border border-yellow-200 rounded-md p-4 flex items-start">
            <AlertCircle className="h-5 w-5 text-yellow-500 mt-0.5 mr-2 flex-shrink-0" />
            <div>
              <h3 className="text-sm font-medium text-yellow-800">자동이체 비활성화됨</h3>
              <p className="text-sm text-yellow-700 mt-1">
                자동이체가 비활성화된 상태입니다. 활성화하려면 위의 스위치를 켜세요.
              </p>
            </div>
          </div>
        )}
      </main>
    </>
  )
}

