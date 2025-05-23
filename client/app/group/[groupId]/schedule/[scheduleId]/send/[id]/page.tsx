"use client"

import type React from "react"

import { useState } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { Header } from "@/components/Header"
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { toast } from "@/components/ui/use-toast"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog"

export default function SendMoneyPage({ params }: { params: { id: string } }) {
  const router = useRouter()
  const searchParams = useSearchParams()
  const groupName = searchParams.get("groupName") || "알 수 없는 그룹"
  const [amount, setAmount] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [pinCode, setPinCode] = useState("")
  const [isConfirmDialogOpen, setIsConfirmDialogOpen] = useState(false)
  const [isPinDialogOpen, setIsPinDialogOpen] = useState(false)

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    setIsConfirmDialogOpen(true)
  }

  const handleConfirm = () => {
    setIsConfirmDialogOpen(false)
    setIsPinDialogOpen(true)
  }

  const handlePinSubmit = async () => {
    if (pinCode !== "000000") {
      toast({
        title: "PIN 오류",
        description: "올바른 PIN 번호를 입력해주세요.",
        variant: "destructive",
      })
      return
    }

    setIsLoading(true)
    setIsPinDialogOpen(false)

    try {
      // 여기에 실제 송금 로직을 구현합니다.
      // API 호출 등을 수행합니다.
      await new Promise((resolve) => setTimeout(resolve, 1500)) // 임시 지연

      toast({
        title: "송금 완료",
        description: `${groupName}에 ${amount}원이 성공적으로 송금되었습니다.`,
      })
      router.push("/profile/auto-transfer")
    } catch (error) {
      toast({
        title: "송금 실패",
        description: "송금 중 오류가 발생했습니다. 다시 시도해주세요.",
        variant: "destructive",
      })
    } finally {
      setIsLoading(false)
    }
  }

  const handlePinInput = (digit: string) => {
    if (pinCode.length < 6) {
      setPinCode((prev) => prev + digit)
    }
  }

  const handlePinDelete = () => {
    setPinCode((prev) => prev.slice(0, -1))
  }

  const handlePinClear = () => {
    setPinCode("")
  }

  return (
    <>
      <Header title="송금하기" showBackButton />
      <main className="flex-1 overflow-auto p-4 pb-16">
        <Card>
          <CardHeader>
            <CardTitle>{groupName}에 송금하기</CardTitle>
            <CardDescription>송금할 금액을 입력해주세요.</CardDescription>
          </CardHeader>
          <form onSubmit={handleSubmit}>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="amount">송금 금액</Label>
                <Input
                  id="amount"
                  type="number"
                  placeholder="0"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  required
                />
              </div>
            </CardContent>
            <CardFooter>
              <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? "송금 중..." : "송금하기"}
              </Button>
            </CardFooter>
          </form>
        </Card>
      </main>

      <Dialog open={isConfirmDialogOpen} onOpenChange={setIsConfirmDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>송금 금액 확인</DialogTitle>
            <DialogDescription>아래 금액이 맞는지 확인해주세요.</DialogDescription>
          </DialogHeader>
          <div className="py-4">
            <p className="text-lg font-semibold text-center">{Number(amount).toLocaleString()}원</p>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsConfirmDialogOpen(false)}>
              취소
            </Button>
            <Button onClick={handleConfirm}>확인</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={isPinDialogOpen} onOpenChange={setIsPinDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>PIN 번호 입력</DialogTitle>
            <DialogDescription>송금을 완료하려면 6자리 PIN 번호를 입력해주세요.</DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <Input
              type="password"
              placeholder="PIN 번호"
              value={pinCode}
              readOnly
              className="text-center text-2xl tracking-widest"
            />
            <div className="grid grid-cols-3 gap-2">
              {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((num) => (
                <Button key={num} onClick={() => handlePinInput(num.toString())} className="text-2xl py-6">
                  {num}
                </Button>
              ))}
              <Button onClick={handlePinClear} className="text-lg py-6">
                Clear
              </Button>
              <Button onClick={() => handlePinInput("0")} className="text-2xl py-6">
                0
              </Button>
              <Button onClick={handlePinDelete} className="text-lg py-6">
                Delete
              </Button>
            </div>
            <Button onClick={handlePinSubmit} className="w-full" disabled={pinCode.length !== 6}>
              확인
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </>
  )
}

