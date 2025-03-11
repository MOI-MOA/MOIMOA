"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Header } from "@/components/Header"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { toast } from "@/components/ui/use-toast"

export default function SignUpPage() {
  const router = useRouter()
  const [step, setStep] = useState(1)
  const [isLoading, setIsLoading] = useState(false)
  const [isVerified, setIsVerified] = useState(false)
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
    pinPassword: "",
    confirmPinPassword: "",
  })

  useEffect(() => {
    if (formData.pinPassword.length === 6 && step === 3) {
      setStep(4)
    }

    if (formData.confirmPinPassword.length === 6 && step === 4) {
      if (formData.confirmPinPassword !== formData.pinPassword) {
        toast({
          title: "PIN 비밀번호 불일치",
          description: "PIN 비밀번호가 일치하지 않습니다. 다시 입력해주세요.",
          variant: "destructive",
        })
        setTimeout(() => {
          setStep(3)
          setFormData((prev) => ({ ...prev, pinPassword: "", confirmPinPassword: "" }))
        }, 1500)
      } else {
        toast({
          title: "PIN 비밀번호 확인 완료",
          description: "PIN 비밀번호가 성공적으로 설정되었습니다.",
        })
      }
    }
  }, [formData.pinPassword, formData.confirmPinPassword, step])

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleVerification = () => {
    setIsVerified(true)
    toast({
      title: "본인인증 완료",
      description: "본인인증이 성공적으로 완료되었습니다.",
    })
  }

  const handleNextStep = () => {
    if (step < 4) setStep(step + 1)
  }

  const handlePrevStep = () => {
    if (step > 1) setStep(step - 1)
  }

  const handlePinInput = (digit: string, field: "pinPassword" | "confirmPinPassword") => {
    if (formData[field].length < 6) {
      const newValue = formData[field] + digit
      setFormData((prev) => ({
        ...prev,
        [field]: newValue,
      }))
    }
  }

  const handlePinDelete = (field: "pinPassword" | "confirmPinPassword") => {
    setFormData((prev) => ({
      ...prev,
      [field]: prev[field].slice(0, -1),
    }))
  }

  const handlePinClear = (field: "pinPassword" | "confirmPinPassword") => {
    setFormData((prev) => ({
      ...prev,
      [field]: "",
    }))
  }

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()

    if (step !== 4) return

    if (formData.pinPassword.length !== 6 || formData.confirmPinPassword.length !== 6) {
      toast({
        title: "PIN 비밀번호 미완료",
        description: "PIN 비밀번호는 6자리여야 합니다.",
        variant: "destructive",
      })
      return
    }

    if (formData.pinPassword !== formData.confirmPinPassword) {
      toast({
        title: "PIN 비밀번호 불일치",
        description: "PIN 비밀번호가 일치하지 않습니다.",
        variant: "destructive",
      })
      return
    }

    setIsLoading(true)

    if (formData.password !== formData.confirmPassword) {
      toast({
        title: "비밀번호 불일치",
        description: "비밀번호와 비밀번호 확인이 일치하지 않습니다.",
        variant: "destructive",
      })
      setIsLoading(false)
      return
    }

    try {
      // 회원가입 API 호출 로직
      await new Promise((resolve) => setTimeout(resolve, 1500))

      toast({
        title: "회원가입 성공",
        description: "회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.",
      })
      router.push("/login")
    } catch (error) {
      toast({
        title: "회원가입 실패",
        description: "회원가입 중 오류가 발생했습니다. 다시 시도해주세요.",
        variant: "destructive",
      })
    } finally {
      setIsLoading(false)
    }
  }

  const renderPinInput = (field: "pinPassword" | "confirmPinPassword") => (
    <>
      <div className="space-y-2">
        <Label htmlFor={field}>
          {field === "pinPassword" ? "PIN 비밀번호 설정 (6자리)" : "PIN 비밀번호 확인 (6자리)"}
        </Label>
        <Input
          id={field}
          name={field}
          type="password"
          maxLength={6}
          value={formData[field]}
          readOnly
          className="text-center text-2xl tracking-widest"
        />
      </div>
      <div className="grid grid-cols-3 gap-2 mt-4">
        {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((num) => (
          <Button key={num} onClick={() => handlePinInput(num.toString(), field)} className="text-2xl py-6">
            {num}
          </Button>
        ))}
        <Button onClick={() => handlePinClear(field)} className="text-lg py-6">
          Clear
        </Button>
        <Button onClick={() => handlePinInput("0", field)} className="text-2xl py-6">
          0
        </Button>
        <Button onClick={() => handlePinDelete(field)} className="text-lg py-6">
          Delete
        </Button>
      </div>
    </>
  )

  const renderStepContent = () => {
    switch (step) {
      case 1:
        return (
          <div className="space-y-4">
            <p>회원가입을 위해 본인인증이 필요합니다.</p>
            <Button onClick={handleVerification} disabled={isVerified}>
              {isVerified ? "인증 완료" : "본인인증"}
            </Button>
          </div>
        )
      case 2:
        return (
          <>
            <div className="space-y-2">
              <Label htmlFor="name">이름</Label>
              <Input
                id="name"
                name="name"
                type="text"
                placeholder="홍길동"
                required
                value={formData.name}
                onChange={handleInputChange}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="email">이메일</Label>
              <Input
                id="email"
                name="email"
                type="email"
                placeholder="example@example.com"
                required
                value={formData.email}
                onChange={handleInputChange}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="password">비밀번호</Label>
              <Input
                id="password"
                name="password"
                type="password"
                required
                value={formData.password}
                onChange={handleInputChange}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="confirmPassword">비밀번호 확인</Label>
              <Input
                id="confirmPassword"
                name="confirmPassword"
                type="password"
                required
                value={formData.confirmPassword}
                onChange={handleInputChange}
              />
            </div>
          </>
        )
      case 3:
        return renderPinInput("pinPassword")
      case 4:
        return renderPinInput("confirmPinPassword")
    }
  }

  return (
    <>
      <Header title="회원가입" showBackButton />
      <main className="flex-1 overflow-auto p-4 flex items-center justify-center">
        <Card className="w-full max-w-md">
          <form onSubmit={handleSubmit}>
            <CardHeader>
              <CardTitle>회원가입 - 단계 {step}/4</CardTitle>
              <CardDescription>새 계정을 만들어 서비스를 이용하세요.</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              {/* Progress bar */}
              <div className="w-full bg-gray-200 rounded-full h-2.5">
                <div className="bg-blue-600 h-2.5 rounded-full" style={{ width: `${(step / 4) * 100}%` }}></div>
              </div>
              {renderStepContent()}
            </CardContent>
            <CardFooter className="flex justify-between">
              {step > 1 && step !== 4 && (
                <Button type="button" onClick={handlePrevStep} variant="outline">
                  이전
                </Button>
              )}
              {step < 3 ? (
                <Button type="button" onClick={handleNextStep} disabled={step === 1 && !isVerified} className="ml-auto">
                  다음
                </Button>
              ) : step === 4 ? (
                <Button
                  type="submit"
                  className="ml-auto"
                  disabled={
                    isLoading ||
                    formData.confirmPinPassword.length !== 6 ||
                    formData.pinPassword !== formData.confirmPinPassword
                  }
                >
                  {isLoading ? "처리 중..." : "가입 완료"}
                </Button>
              ) : null}
            </CardFooter>
          </form>
        </Card>
      </main>
    </>
  )
}

