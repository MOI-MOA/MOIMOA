"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { Header } from "@/components/Header"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
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
import {
  CreditCard,
  DollarSign,
  MessageSquare,
  Send,
  RefreshCw,
  Lock,
  CheckCircle2,
  XCircle,
  Trash2,
  AlertCircle,
  User
} from "lucide-react"
import axios from "axios"

export default function SendMoneyPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const [amount, setAmount] = useState("")
  const [accountNumber, setAccountNumber] = useState("")
  const [message, setMessage] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [pinCode, setPinCode] = useState("")
  const [isConfirmDialogOpen, setIsConfirmDialogOpen] = useState(false)
  const [isPinDialogOpen, setIsPinDialogOpen] = useState(false)
  const [accountName, setAccountName] = useState("")
  const [isAccountCheckDialogOpen, setIsAccountCheckDialogOpen] = useState(false)

  useEffect(() => {
    const account = searchParams.get('account')
    const cost = searchParams.get('cost')
    
    if (account) {
      setAccountNumber(account)
    }
    if (cost) {
      setAmount(cost)
    }
  }, [searchParams])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    try {
      const response = await axios.post('/api/v1/gathering-account/account/check', {
        accountNo: accountNumber
      })
      
      if (response.status === 200) {
        setAccountName(response.data.name)
        setIsAccountCheckDialogOpen(true)
      }
    } catch (error: any) {
      if (error.response?.status === 404) {
        toast({
          title: "계좌 확인 실패",
          description: "존재하지 않는 계좌번호입니다. 다시 확인해주세요.",
          variant: "destructive",
        })
      } else {
        toast({
          title: "오류 발생",
          description: "계좌 확인 중 오류가 발생했습니다. 다시 시도해주세요.",
          variant: "destructive",
        })
      }
    }
  }

  const handleAccountCheckConfirm = () => {
    setIsAccountCheckDialogOpen(false)
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
        description: `${accountNumber}로 ${amount}원이 성공적으로 송금되었습니다.`,
      })
      router.back()
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
      <main className="flex-1 overflow-auto p-4 pb-16 bg-slate-50">
        <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
          <CardHeader className="border-b border-slate-100 bg-white">
            <div className="flex items-center gap-2 mb-2">
              <Send className="h-5 w-5 text-blue-600" />
              <CardTitle className="text-slate-800">송금하기</CardTitle>
            </div>
            <CardDescription className="text-slate-500">
              송금할 계좌와 금액을 입력해주세요.
            </CardDescription>
          </CardHeader>
          <CardContent className="p-6 space-y-5">
            <form onSubmit={handleSubmit}>
              <div className="space-y-5">
                <div className="space-y-2">
                  <Label htmlFor="accountNumber" className="text-slate-700 flex items-center">
                    <CreditCard className="h-4 w-4 mr-1.5 text-slate-500" />
                    계좌번호
                  </Label>
                  <div className="relative">
                    <Input
                      id="accountNumber"
                      placeholder="계좌번호를 입력하세요"
                      value={accountNumber}
                      onChange={(e) => setAccountNumber(e.target.value)}
                      required
                      className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                    />
                    <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                      <CreditCard className="h-5 w-5 text-slate-400" />
                    </div>
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="amount" className="text-slate-700 flex items-center">
                    <DollarSign className="h-4 w-4 mr-1.5 text-slate-500" />
                    송금 금액
                  </Label>
                  <div className="relative">
                    <Input
                      id="amount"
                      type="number"
                      placeholder="0"
                      value={amount}
                      onChange={(e) => setAmount(e.target.value)}
                      required
                      className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                    />
                    <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                      <DollarSign className="h-5 w-5 text-slate-400" />
                    </div>
                    <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                      <span className="text-slate-400">원</span>
                    </div>
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="message" className="text-slate-700 flex items-center">
                    <MessageSquare className="h-4 w-4 mr-1.5 text-slate-500" />
                    메시지
                  </Label>
                  <div className="relative">
                    <Input
                      id="message"
                      placeholder="상대방에게 보여질 메시지를 입력하세요"
                      value={message}
                      onChange={(e) => setMessage(e.target.value)}
                      maxLength={50}
                      className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                    />
                    <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                      <MessageSquare className="h-5 w-5 text-slate-400" />
                    </div>
                  </div>
                </div>
              </div>

              <Button 
                type="submit" 
                className="w-full mt-6 py-6 rounded-xl bg-blue-600 hover:bg-blue-700"
                disabled={isLoading}
              >
                {isLoading ? (
                  <div className="flex items-center">
                    <RefreshCw className="h-4 w-4 mr-2 animate-spin" />
                    송금 중...
                  </div>
                ) : (
                  <div className="flex items-center">
                    <Send className="h-4 w-4 mr-2" />
                    송금하기
                  </div>
                )}
              </Button>
            </form>
          </CardContent>
        </Card>

        {/* 계좌 확인 다이얼로그 */}
        <Dialog open={isAccountCheckDialogOpen} onOpenChange={setIsAccountCheckDialogOpen}>
          <DialogContent className="sm:max-w-md rounded-xl">
            <DialogHeader>
              <DialogTitle className="flex items-center text-slate-800">
                <User className="h-5 w-5 mr-2 text-blue-600" />
                계좌 확인
              </DialogTitle>
              <DialogDescription>아래 계좌로 송금하시겠습니까?</DialogDescription>
            </DialogHeader>
            <div className="py-4 space-y-4">
              <div className="bg-blue-50 p-4 rounded-xl border border-blue-100">
                <p className="text-lg font-semibold text-center text-blue-800">{accountName}에게 보냅니다</p>
                <p className="text-sm text-center text-blue-600 mt-1">
                  계좌번호: <span className="font-medium">{accountNumber}</span>
                </p>
                {message && (
                  <p className="text-sm text-center text-blue-600 mt-1">
                    메시지: <span className="font-medium">{message}</span>
                  </p>
                )}
              </div>
            </div>
            <DialogFooter className="gap-2 sm:gap-0">
              <Button 
                variant="outline" 
                onClick={() => setIsAccountCheckDialogOpen(false)}
                className="rounded-xl border-slate-200"
              >
                취소
              </Button>
              <Button 
                onClick={handleAccountCheckConfirm}
                className="rounded-xl bg-blue-600 hover:bg-blue-700"
              >
                확인
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>

        {/* 송금 정보 확인 다이얼로그 */}
        <Dialog open={isConfirmDialogOpen} onOpenChange={setIsConfirmDialogOpen}>
          <DialogContent className="sm:max-w-md rounded-xl">
            <DialogHeader>
              <DialogTitle className="flex items-center text-slate-800">
                <AlertCircle className="h-5 w-5 mr-2 text-blue-600" />
                송금 정보 확인
              </DialogTitle>
              <DialogDescription>아래 정보가 맞는지 확인해주세요.</DialogDescription>
            </DialogHeader>
            <div className="py-4 space-y-4">
              <div className="bg-slate-50 p-4 rounded-xl border border-slate-200">
                <div className="space-y-3">
                  <p className="text-sm text-slate-600">
                    계좌번호: <span className="font-medium text-slate-800">{accountNumber}</span>
                  </p>
                  <p className="text-xl font-semibold text-center text-blue-600">
                    {Number(amount).toLocaleString()}원
                  </p>
                  {message && (
                    <p className="text-sm text-slate-600">
                      메시지: <span className="font-medium text-slate-800">{message}</span>
                    </p>
                  )}
                </div>
              </div>
            </div>
            <DialogFooter className="gap-2 sm:gap-0">
              <Button 
                variant="outline" 
                onClick={() => setIsConfirmDialogOpen(false)}
                className="rounded-xl border-slate-200"
              >
                취소
              </Button>
              <Button 
                onClick={handleConfirm}
                className="rounded-xl bg-blue-600 hover:bg-blue-700"
              >
                확인
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>

        {/* PIN 입력 다이얼로그 */}
        <Dialog open={isPinDialogOpen} onOpenChange={setIsPinDialogOpen}>
          <DialogContent className="sm:max-w-md rounded-xl">
            <DialogHeader>
              <div className="text-center">
                <div className="bg-blue-100 w-12 h-12 rounded-full flex items-center justify-center mx-auto mb-4">
                  <Lock className="h-6 w-6 text-blue-600" />
                </div>
                <DialogTitle className="text-slate-800">PIN 번호 입력</DialogTitle>
                <DialogDescription>
                  송금을 완료하려면 6자리 PIN 번호를 입력해주세요.
                </DialogDescription>
              </div>
            </DialogHeader>
            <div className="space-y-5 py-4">
              <div className="flex justify-center mb-6">
                <div className="flex items-center gap-2">
                  {Array.from({ length: 6 }).map((_, i) => (
                    <div
                      key={i}
                      className={`w-10 h-10 border-2 rounded-lg flex items-center justify-center ${
                        i < pinCode.length
                          ? "bg-blue-100 border-blue-300"
                          : "border-slate-200"
                      }`}
                    >
                      {i < pinCode.length ? "•" : ""}
                    </div>
                  ))}
                </div>
              </div>

              <div className="grid grid-cols-3 gap-3">
                {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((num) => (
                  <Button
                    key={num}
                    onClick={() => handlePinInput(num.toString())}
                    className="text-xl font-medium py-6 rounded-xl hover:bg-blue-50 border border-slate-200 bg-white text-slate-800"
                    variant="outline"
                  >
                    {num}
                  </Button>
                ))}
                <Button 
                  onClick={handlePinClear} 
                  className="text-sm py-6 rounded-xl hover:bg-red-50 border border-slate-200 bg-white text-red-600"
                  variant="outline"
                >
                  <Trash2 className="h-5 w-5" />
                </Button>
                <Button
                  onClick={() => handlePinInput("0")}
                  className="text-xl font-medium py-6 rounded-xl hover:bg-blue-50 border border-slate-200 bg-white text-slate-800"
                  variant="outline"
                >
                  0
                </Button>
                <Button 
                  onClick={handlePinDelete} 
                  className="text-sm py-6 rounded-xl hover:bg-slate-100 border border-slate-200 bg-white text-slate-700"
                  variant="outline"
                >
                  <XCircle className="h-5 w-5" />
                </Button>
              </div>

              <Button 
                onClick={handlePinSubmit} 
                className="w-full py-6 rounded-xl bg-blue-600 hover:bg-blue-700"
                disabled={pinCode.length !== 6}
              >
                <div className="flex items-center justify-center">
                  <CheckCircle2 className="h-5 w-5 mr-2" />
                  확인
                </div>
              </Button>
            </div>
          </DialogContent>
        </Dialog>
      </main>
    </>
  )
}

