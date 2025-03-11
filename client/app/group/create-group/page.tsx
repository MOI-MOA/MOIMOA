"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { ArrowRight } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Card, CardContent } from "@/components/ui/card"
import { toast } from "@/components/ui/use-toast"
import { Header } from "@/components/Header"

export default function CreateGroupPage() {
  const router = useRouter()
  const [step, setStep] = useState(1)
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    members: "",
    paybackPercent: "",
    monthlyDepositDay: "",
    monthlyDepositAmount: "",
    depositAmount: "",
  })
  const [isLoading, setIsLoading] = useState(false)

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleNext = () => {
    if (step < 3) {
      setStep((prev) => prev + 1)
    }
  }

  const handlePrevious = () => {
    if (step > 1) {
      setStep((prev) => prev - 1)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setIsLoading(true)
    try {
      // Here you would typically send the data to your backend
      // const response = await fetch('/api/create-group', { method: 'POST', body: JSON.stringify(formData) })
      // if (!response.ok) throw new Error('Failed to create group')
      console.log(formData)
      toast({
        title: "모임 생성 완료",
        description: "새로운 모임이 성공적으로 생성되었습니다.",
      })
      router.push("/")
    } catch (error) {
      console.error(error)
      toast({
        title: "오류 발생",
        description: "모임 생성 중 문제가 발생했습니다. 다시 시도해주세요.",
        variant: "destructive",
      })
    } finally {
      setIsLoading(false)
    }
  }

  const renderStep = () => {
    switch (step) {
      case 1:
        return (
          <>
            <div>
              <Label htmlFor="title">모임 이름</Label>
              <Input
                id="title"
                name="title"
                value={formData.title}
                onChange={handleInputChange}
                placeholder="모임 이름을 입력하세요"
                required
              />
            </div>
            <div>
              <Label htmlFor="description">모임 소개</Label>
              <Textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                placeholder="모임에 대한 간단한 소개를 입력하세요"
                required
              />
            </div>
            <div>
              <Label htmlFor="members">참여 인원</Label>
              <Input
                id="members"
                name="members"
                type="number"
                value={formData.members}
                onChange={handleInputChange}
                placeholder="참여 인원 수를 입력하세요"
                required
              />
            </div>
          </>
        )
      case 2:
        return (
          <>
            <div>
              <Label htmlFor="paybackPercent">페이백 퍼센트 (0-100%)</Label>
              <Input
                id="paybackPercent"
                name="paybackPercent"
                type="number"
                min="0"
                max="100"
                value={formData.paybackPercent}
                onChange={handleInputChange}
                placeholder="페이백 퍼센트를 입력하세요"
                required
              />
            </div>
            <div>
              <Label htmlFor="monthlyDepositDay">매월 입금일</Label>
              <Input
                id="monthlyDepositDay"
                name="monthlyDepositDay"
                type="number"
                min="1"
                max="31"
                value={formData.monthlyDepositDay}
                onChange={handleInputChange}
                placeholder="매월 입금일을 입력하세요"
                required
              />
            </div>
          </>
        )
      case 3:
        return (
          <>
            <div>
              <Label htmlFor="monthlyDepositAmount">매월 입금 금액</Label>
              <Input
                id="monthlyDepositAmount"
                name="monthlyDepositAmount"
                type="number"
                value={formData.monthlyDepositAmount}
                onChange={handleInputChange}
                placeholder="매월 입금 금액을 입력하세요"
                required
              />
            </div>
            <div>
              <Label htmlFor="depositAmount">보증금 금액</Label>
              <Input
                id="depositAmount"
                name="depositAmount"
                type="number"
                value={formData.depositAmount}
                onChange={handleInputChange}
                placeholder="보증금 금액을 입력하세요"
                required
              />
            </div>
          </>
        )
      default:
        return null
    }
  }

  return (
    <>
      <Header title="새 모임 만들기" showBackButton />
      <main className="flex-1 overflow-auto p-4 pb-16">
        <Card>
          <CardContent className="p-6">
            <div className="mb-6 text-center">
              <h2 className="text-xl font-semibold">Step {step} of 3</h2>
              <div className="w-full bg-gray-200 rounded-full h-2.5 mt-2">
                <div className="bg-blue-600 h-2.5 rounded-full" style={{ width: `${(step / 3) * 100}%` }}></div>
              </div>
            </div>
            <form onSubmit={handleSubmit} className="space-y-4">
              {renderStep()}
              <div className="flex justify-between mt-6">
                {step > 1 && (
                  <Button type="button" onClick={handlePrevious} disabled={isLoading}>
                    이전
                  </Button>
                )}
                {step < 3 ? (
                  <Button type="button" onClick={handleNext} className="ml-auto" disabled={isLoading}>
                    다음
                    <ArrowRight className="h-4 w-4 ml-2" />
                  </Button>
                ) : (
                  <Button
                    type="submit"
                    className="ml-auto bg-blue-500 hover:bg-blue-600 text-white"
                    disabled={isLoading}
                  >
                    {isLoading ? "처리 중..." : "모임 만들기"}
                  </Button>
                )}
              </div>
            </form>
          </CardContent>
        </Card>
      </main>
    </>
  )
}

