"use client"
import { useRouter } from "next/navigation"
import { Plus, User, Clock, Settings, CalendarIcon, Users, Wallet, AlertCircle } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Header } from "@/components/Header"
import React, { use, useState } from "react"
import { toast } from "@/components/ui/use-toast"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"

export default function GroupDetailPage({ params }: { params: Promise<{ groupId: string }> }) {
  const router = useRouter()
  const { groupId } = use(params)
  // 상태 변수 추가 부분 수정
  const [hasManagerRequest, setHasManagerRequest] = useState(true) // 실제로는 API에서 확인해야 함
  const [isConfirmDialogOpen, setIsConfirmDialogOpen] = useState(false)
  const [confirmAction, setConfirmAction] = useState<"accept" | "reject" | null>(null)
  const [isPinDialogOpen, setIsPinDialogOpen] = useState(false)
  const [isConfirmPinDialogOpen, setIsConfirmPinDialogOpen] = useState(false)
  const [pinCode, setPinCode] = useState("")
  const [confirmPinCode, setConfirmPinCode] = useState("")
  const [isProcessing, setIsProcessing] = useState(false)
  const [isLeaveDialogOpen, setIsLeaveDialogOpen] = useState(false)

  // Mock data for the group
  const groupData = {
    id: groupId,
    name: "회사 동료",
    description: "월간 회식 및 경비",
    totalMembers: 8,
    manager: {
      name: "김철수",
      avatar: "/placeholder.svg?height=40&width=40",
    },
    requestingManager: {
      name: "이영희", // 총무 위임을 요청한 사람
      avatar: "/placeholder.svg?height=40&width=40",
    },
    accounts: {
      groupBalance: 2400000,
      myBalance: 300000,
      myDeposit: 100000,
    },
    nextMeeting: "2024-03-15",
    monthlyFee: 30000,
    paymentStatus: "완료",
    schedules: [
      {
        id: 1,
        date: "2024-03-15",
        time: "19:00",
        title: "3월 정기 회식",
        participants: 6,
        budgetPerPerson: 50000,
        totalBudget: 300000,
        location: "강남역 인근",
      },
      {
        id: 2,
        date: "2024-03-28",
        time: "09:00",
        title: "팀 워크샵",
        participants: 8,
        budgetPerPerson: 100000,
        totalBudget: 800000,
        location: "제주도",
      },
    ],
    isManager: true, // 현재 사용자가 총무인지 여부 (실제로는 백엔드에서 확인해야 함)
  }

  // 총무 위임 수락 처리 함수 수정
  const handleAcceptManagerRequest = () => {
    setIsConfirmDialogOpen(false)
    setPinCode("")
    setIsPinDialogOpen(true)
  }

  // PIN 입력 함수 추가
  const handlePinInput = (digit: string) => {
    if (pinCode.length < 6) {
      setPinCode((prev) => prev + digit)
    }

    // 6자리 입력 완료 시 자동으로 확인 PIN 입력으로 넘어감
    if (pinCode.length === 5) {
      setTimeout(() => {
        setIsPinDialogOpen(false)
        setConfirmPinCode("")
        setIsConfirmPinDialogOpen(true)
      }, 300)
    }
  }

  // 확인 PIN 입력 함수 추가
  const handleConfirmPinInput = (digit: string) => {
    if (confirmPinCode.length < 6) {
      setConfirmPinCode((prev) => prev + digit)
    }
  }

  // PIN 삭제 함수 추가
  const handlePinDelete = (isPinConfirm: boolean) => {
    if (isPinConfirm) {
      setConfirmPinCode((prev) => prev.slice(0, -1))
    } else {
      setPinCode((prev) => prev.slice(0, -1))
    }
  }

  // PIN 초기화 함수 추가
  const handlePinClear = (isPinConfirm: boolean) => {
    if (isPinConfirm) {
      setConfirmPinCode("")
    } else {
      setPinCode("")
    }
  }

  // PIN 확인 함수 추가
  const handlePinConfirm = async () => {
    if (confirmPinCode.length !== 6) {
      return
    }

    if (pinCode !== confirmPinCode) {
      toast({
        title: "PIN 번호 불일치",
        description: "PIN 번호가 일치하지 않습니다. 다시 시도해주세요.",
        variant: "destructive",
      })
      setIsConfirmPinDialogOpen(false)
      setPinCode("")
      setConfirmPinCode("")
      setIsPinDialogOpen(true)
      return
    }

    setIsProcessing(true)

    try {
      // 실제로는 API 호출로 처리
      await new Promise((resolve) => setTimeout(resolve, 1500)) // 임시 지연

      setHasManagerRequest(false)
      toast({
        title: "총무 위임 수락",
        description: `${groupData.requestingManager.name}님에게 총무 권한이 위임되었습니다.`,
      })
    } catch (error) {
      toast({
        title: "오류 발생",
        description: "처리 중 오류가 발생했습니다. 다시 시도해주세요.",
        variant: "destructive",
      })
    } finally {
      setIsProcessing(false)
      setIsConfirmPinDialogOpen(false)
      setPinCode("")
      setConfirmPinCode("")
    }
  }

  // 총무 위임 거절 처리 함수
  const handleRejectManagerRequest = () => {
    // 실제로는 API 호출로 처리
    setHasManagerRequest(false)
    toast({
      title: "총무 위임 거절",
      description: "총무 위임 요청이 거절되었습니다.",
    })
    setIsConfirmDialogOpen(false)
  }

  // 확인 다이얼로그 열기 함수
  const openConfirmDialog = (action: "accept" | "reject") => {
    setConfirmAction(action)
    setIsConfirmDialogOpen(true)
  }

  // 모임 탈퇴 처리 함수
  const handleLeaveGroup = async () => {
    try {
      // API 호출로 모임 탈퇴 처리
      await fetch(`/api/groups/${groupId}/leave`, {
        method: 'POST',
      });
      
      toast({
        title: "모임 탈퇴 완료",
        description: "모임에서 탈퇴되었습니다.",
      })
      
      router.push('/') // 메인 페이지로 이동
    } catch (error) {
      toast({
        title: "오류 발생",
        description: "탈퇴 처리 중 오류가 발생했습니다. 다시 시도해주세요.",
        variant: "destructive",
      })
    }
    setIsLeaveDialogOpen(false)
  }

  return (
    <>
      <Header title={groupData.name} showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-20">
        {/* 총무 위임 요청 알림 */}
        {hasManagerRequest && (
          <Card className="bg-yellow-50 border-yellow-200">
            <CardContent className="p-4">
              <div className="flex items-center space-x-2 mb-2">
                <AlertCircle className="h-5 w-5 text-yellow-500" />
                <h3 className="font-medium">총무 위임 요청</h3>
              </div>
              <div className="flex items-center justify-between">
                <div className="flex items-center">
                  <Avatar className="h-8 w-8 mr-2">
                    <AvatarImage src={groupData.requestingManager.avatar} />
                    <AvatarFallback>{groupData.requestingManager.name.slice(0, 2)}</AvatarFallback>
                  </Avatar>
                  <span>{groupData.requestingManager.name}님이 총무 위임을 요청했습니다.</span>
                </div>
                <div className="flex space-x-2">
                  <Button
                    variant="outline"
                    size="sm"
                    className="text-red-600 border-red-200 hover:bg-red-50"
                    onClick={() => openConfirmDialog("reject")}
                  >
                    거절
                  </Button>
                  <Button
                    size="sm"
                    className="bg-green-600 hover:bg-green-700"
                    onClick={() => openConfirmDialog("accept")}
                  >
                    수락
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        )}

        {/* Group Info */}
        <Card>
          <CardContent className="p-4">
            <div className="flex items-center justify-between mb-4">
              <div>
                <h2 className="font-medium text-lg">{groupData.description}</h2>
                <p className="text-sm text-gray-500">다음 모임: {groupData.nextMeeting}</p>
              </div>
              <div className="flex flex-col items-end">
                <div className="flex items-center text-sm text-gray-600">
                  <User className="h-4 w-4 mr-1" />
                  총무
                </div>
                <div className="flex items-center mt-1">
                  <Avatar className="h-6 w-6 mr-2">
                    <AvatarImage src={groupData.manager.avatar} />
                    <AvatarFallback>KM</AvatarFallback>
                  </Avatar>
                  <span className="text-sm">{groupData.manager.name}</span>
                </div>
              </div>
            </div>
            {groupData.isManager && (
              <Button
                variant="outline"
                className="w-full mt-2"
                onClick={() => router.push(`/group/${groupId}/members`)}
              >
                <Settings className="h-4 w-4 mr-2" />
                회원 관리
              </Button>
            )}
            {groupData.isManager && (
              <Button
                variant="outline"
                className="w-full mt-2"
                onClick={() => router.push(`/group/${groupId}/member-list`)}
              >
                <Users className="h-4 w-4 mr-2" />
                회원 목록
              </Button>
            )}
          </CardContent>
        </Card>

        {/* 나머지 기존 코드는 그대로 유지 */}
        {/* Account Info */}
        <div className="grid grid-cols-3 gap-3">
          <Card>
            <CardContent className="p-3">
              <div className="text-xs text-gray-500 mb-1">모임계좌 잔액</div>
              <div className="font-semibold text-blue-600">{groupData.accounts.groupBalance.toLocaleString()}원</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-3">
              <div className="text-xs text-gray-500 mb-1">내 계좌 잔액</div>
              <div className="font-semibold text-green-600">{groupData.accounts.myBalance.toLocaleString()}원</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-3">
              <div className="text-xs text-gray-500 mb-1">보증금</div>
              <div className="font-semibold">{groupData.accounts.myDeposit.toLocaleString()}원</div>
            </CardContent>
          </Card>
        </div>
        <Button
          variant="outline"
          className="w-full mt-2"
          onClick={() => router.push(`/group/${groupId}/account-history`)}
        >
          <Wallet className="h-4 w-4 mr-2" />
          모임통장 내역 보기
        </Button>

        {/* Monthly Status */}
        <Card>
          <CardContent className="p-4">
            <div className="flex justify-between items-center mb-2">
              <div className="text-sm text-gray-600">이번 달 모임비</div>
              <Badge variant="outline" className="text-green-600">
                {groupData.paymentStatus}
              </Badge>
            </div>
            <div className="text-lg font-semibold">{groupData.monthlyFee.toLocaleString()}원</div>
          </CardContent>
        </Card>

        {/* Schedules */}
        <div className="space-y-3">
          <div className="flex justify-between items-center">
            <h2 className="text-lg font-semibold">일정</h2>
            <Button onClick={() => router.push(`/group/${groupId}/create-schedule`)}>
              <Plus className="h-4 w-4 mr-2" />새 일정 만들기
            </Button>
          </div>

          {groupData.schedules.map((schedule) => (
            <Card
              key={schedule.id}
              className="hover:shadow-md transition-shadow cursor-pointer"
              onClick={() => router.push(`/group/${groupId}/schedule/${schedule.id}`)}
            >
              <CardContent className="p-4">
                <div className="flex justify-between items-start mb-2">
                  <div className="font-medium">{schedule.title}</div>
                  <Badge variant="secondary">#{schedule.id}차</Badge>
                </div>
                <div className="grid grid-cols-2 gap-2 text-sm text-gray-600">
                  <div className="flex items-center">
                    <CalendarIcon className="h-4 w-4 mr-1" />
                    {schedule.date}
                  </div>
                  <div className="flex items-center">
                    <Users className="h-4 w-4 mr-1" />
                    {schedule.participants}명
                  </div>
                  <div className="flex items-center">
                    <Wallet className="h-4 w-4 mr-1" />
                    {schedule.budgetPerPerson.toLocaleString()}원/인
                  </div>
                  <div className="flex items-center">
                    <Clock className="h-4 w-4 mr-1" />
                    {schedule.location}
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        {/* 모임 탈퇴 버튼 */}
        <Button
          variant="outline"
          className="w-full mt-4 border-red-200 text-red-600 hover:bg-red-50"
          onClick={() => setIsLeaveDialogOpen(true)}
        >
          모임 탈퇴
        </Button>
      </main>

      {/* 확인 다이얼로그 */}
      <Dialog open={isConfirmDialogOpen} onOpenChange={setIsConfirmDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{confirmAction === "accept" ? "총무 위임 수락" : "총무 위임 거절"}</DialogTitle>
            <DialogDescription>
              {confirmAction === "accept"
                ? `${groupData.requestingManager.name}님에게 총무를 위임하시겠습니까? 이 작업은 되돌릴 수 없습니다.`
                : `${groupData.requestingManager.name}님의 총무 위임 요청을 거절하시겠습니까?`}
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsConfirmDialogOpen(false)}>
              취소
            </Button>
            <Button
              onClick={confirmAction === "accept" ? handleAcceptManagerRequest : handleRejectManagerRequest}
              variant={confirmAction === "accept" ? "default" : "destructive"}
            >
              확인
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* PIN 입력 다이얼로그 */}
      <Dialog open={isPinDialogOpen} onOpenChange={setIsPinDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>PIN 번호 입력</DialogTitle>
            <DialogDescription>총무 위임을 완료하려면 6자리 PIN 번호를 입력해주세요.</DialogDescription>
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
              <Button onClick={() => handlePinClear(false)} className="text-lg py-6">
                Clear
              </Button>
              <Button onClick={() => handlePinInput("0")} className="text-2xl py-6">
                0
              </Button>
              <Button onClick={() => handlePinDelete(false)} className="text-lg py-6">
                Delete
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>

      {/* PIN 확인 다이얼로그 */}
      <Dialog open={isConfirmPinDialogOpen} onOpenChange={setIsConfirmPinDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>PIN 번호 확인</DialogTitle>
            <DialogDescription>PIN 번호를 한번 더 입력해주세요.</DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <Input
              type="password"
              placeholder="PIN 번호 확인"
              value={confirmPinCode}
              readOnly
              className="text-center text-2xl tracking-widest"
            />
            <div className="grid grid-cols-3 gap-2">
              {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((num) => (
                <Button key={num} onClick={() => handleConfirmPinInput(num.toString())} className="text-2xl py-6">
                  {num}
                </Button>
              ))}
              <Button onClick={() => handlePinClear(true)} className="text-lg py-6">
                Clear
              </Button>
              <Button onClick={() => handleConfirmPinInput("0")} className="text-2xl py-6">
                0
              </Button>
              <Button onClick={() => handlePinDelete(true)} className="text-lg py-6">
                Delete
              </Button>
            </div>
            <Button
              onClick={handlePinConfirm}
              className="w-full"
              disabled={confirmPinCode.length !== 6 || isProcessing}
            >
              {isProcessing ? "처리 중..." : "확인"}
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      {/* 모임 탈퇴 확인 다이얼로그 */}
      <Dialog open={isLeaveDialogOpen} onOpenChange={setIsLeaveDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>모임 탈퇴</DialogTitle>
            <DialogDescription>
              정말로 {groupData.name} 모임을 탈퇴하시겠습니까?
              탈퇴 시 모임 내역과 보증금이 삭제되며, 이 작업은 되돌릴 수 없습니다.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsLeaveDialogOpen(false)}>
              취소
            </Button>
            <Button variant="destructive" onClick={handleLeaveGroup}>
              탈퇴하기
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  )
}

