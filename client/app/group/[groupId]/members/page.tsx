"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Shield, AlertCircle, Check, X, UserPlus, Link, Copy, Share2, Crown } from "lucide-react"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { toast } from "@/components/ui/use-toast"
import { Header } from "@/components/Header"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog"

export default function GroupMembersPage({ params }: { params: { groupId: string } }) {
  const router = useRouter()
  const groupId = params.groupId

  // 현재 사용자가 총무인지 여부 (실제로는 API에서 확인해야 함)
  const [isManager, setIsManager] = useState(true)

  // 초대 링크 관련 상태
  const [isInviteDialogOpen, setIsInviteDialogOpen] = useState(false)
  const [inviteLink, setInviteLink] = useState("")

  // 총무 위임 관련 상태
  const [selectedMember, setSelectedMember] = useState<any>(null)
  const [isTransferDialogOpen, setIsTransferDialogOpen] = useState(false)
  const [isPasswordDialogOpen, setIsPasswordDialogOpen] = useState(false)
  const [isPinDialogOpen, setIsPinDialogOpen] = useState(false)
  const [password, setPassword] = useState("")
  const [pinCode, setPinCode] = useState("")
  const [isProcessing, setIsProcessing] = useState(false)

  // Mock data for members
  const [members, setMembers] = useState([
    {
      id: 1,
      name: "김철수",
      role: "총무",
      avatar: "/placeholder.svg?height=40&width=40",
      balance: 300000,
      deposit: 100000,
      monthlyFee: {
        amount: 30000,
        status: "완료",
      },
      isManager: true,
    },
    {
      id: 2,
      name: "이영희",
      avatar: "/placeholder.svg?height=40&width=40",
      balance: 250000,
      deposit: 100000,
      monthlyFee: {
        amount: 30000,
        status: "완료",
      },
    },
    {
      id: 3,
      name: "박지성",
      avatar: "/placeholder.svg?height=40&width=40",
      balance: 180000,
      deposit: 100000,
      monthlyFee: {
        amount: 30000,
        status: "미납",
      },
    },
    {
      id: 4,
      name: "최민수",
      avatar: "/placeholder.svg?height=40&width=40",
      balance: 420000,
      deposit: 100000,
      monthlyFee: {
        amount: 30000,
        status: "완료",
      },
    },
  ])

  // 신청 인원 데이터 (실제로는 API에서 가져와야 함)
  const [pendingMembers, setPendingMembers] = useState([
    {
      id: 101,
      name: "홍길동",
      avatar: "/placeholder.svg?height=40&width=40",
      email: "hong@example.com",
      requestDate: "2024-03-20",
    },
    {
      id: 102,
      name: "김영수",
      avatar: "/placeholder.svg?height=40&width=40",
      email: "kim@example.com",
      requestDate: "2024-03-22",
    },
    {
      id: 103,
      name: "이지은",
      avatar: "/placeholder.svg?height=40&width=40",
      email: "lee@example.com",
      requestDate: "2024-03-25",
    },
  ])

  // 초대 링크 생성 함수
  const generateInviteLink = () => {
    // 실제로는 API 호출로 고유한 초대 코드를 생성해야 함
    const inviteCode = Math.random().toString(36).substring(2, 10)
    const link = `${window.location.origin}/group/join?code=${inviteCode}&groupId=${groupId}`
    setInviteLink(link)
    setIsInviteDialogOpen(true)
  }

  // 초대 링크 복사 함수
  const copyInviteLink = () => {
    navigator.clipboard.writeText(inviteLink)
    toast({
      title: "링크 복사 완료",
      description: "초대 링크가 클립보드에 복사되었습니다.",
      duration: 3000,
    })
  }

  // 초대 링크 공유 함수
  const shareInviteLink = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: "모임 초대",
          text: "모임에 참여해보세요!",
          url: inviteLink,
        })
        toast({
          title: "공유 완료",
          description: "초대 링크가 공유되었습니다.",
          duration: 3000,
        })
      } catch (error) {
        console.error("공유 실패:", error)
      }
    } else {
      copyInviteLink()
    }
  }

  // 회원 클릭 함수
  const handleMemberClick = (member) => {
    if (isManager && !member.isManager) {
      setSelectedMember(member)
    }
  }

  // 총무 위임 확인 함수
  const handleTransferConfirm = () => {
    setIsTransferDialogOpen(false)
    setIsPasswordDialogOpen(true)
  }

  // 비밀번호 확인 함수
  const handlePasswordConfirm = () => {
    // 실제로는 비밀번호 검증 로직이 필요함
    setIsPasswordDialogOpen(false)
    setPinCode("")
    setIsPinDialogOpen(true)
  }

  // PIN 입력 함수
  const handlePinInput = (digit: string) => {
    if (pinCode.length < 6) {
      setPinCode((prev) => prev + digit)
    }
  }

  // PIN 삭제 함수
  const handlePinDelete = () => {
    setPinCode((prev) => prev.slice(0, -1))
  }

  // PIN 초기화 함수
  const handlePinClear = () => {
    setPinCode("")
  }

  // PIN 확인 함수
  const handlePinConfirm = async () => {
    if (pinCode !== "000000") {
      toast({
        title: "PIN 번호 오류",
        description: "올바른 PIN 번호를 입력해주세요.",
        variant: "destructive",
      })
      return
    }

    setIsProcessing(true)

    try {
      // 실제로는 API 호출로 총무 위임 처리
      await new Promise((resolve) => setTimeout(resolve, 1500))

      // 총무 권한 변경
      setMembers((prev) =>
        prev.map((member) => {
          if (member.isManager) {
            return { ...member, isManager: false, role: undefined }
          }
          if (member.id === selectedMember.id) {
            return { ...member, isManager: true, role: "총무" }
          }
          return member
        }),
      )

      setIsPinDialogOpen(false)

      toast({
        title: "총무 위임 완료",
        description: `${selectedMember.name}님에게 총무가 위임되었습니다.`,
        duration: 3000,
      })

      // 현재 사용자는 더 이상 총무가 아님
      setIsManager(false)
    } catch (error) {
      toast({
        title: "총무 위임 실패",
        description: "총무 위임 중 오류가 발생했습니다. 다시 시도해주세요.",
        variant: "destructive",
      })
    } finally {
      setIsProcessing(false)
      setPinCode("")
      setPassword("")
      setSelectedMember(null)
    }
  }

  // 신청 수락 함수
  const handleAccept = (memberId) => {
    // 실제로는 API 호출로 처리해야 함
    const memberToAccept = pendingMembers.find((m) => m.id === memberId)

    if (memberToAccept) {
      // 신청 인원 목록에서 제거
      setPendingMembers((prev) => prev.filter((m) => m.id !== memberId))

      // 회원 목록에 추가
      setMembers((prev) => [
        ...prev,
        {
          id: memberToAccept.id,
          name: memberToAccept.name,
          avatar: memberToAccept.avatar,
          balance: 0,
          deposit: 100000,
          monthlyFee: {
            amount: 30000,
            status: "미납",
          },
        },
      ])

      toast({
        title: "회원 신청 수락",
        description: `${memberToAccept.name}님의 회원 신청이 수락되었습니다.`,
        duration: 3000,
      })
    }
  }

  // 신청 거절 함수
  const handleReject = (memberId) => {
    // 실제로는 API 호출로 처리해야 함
    const memberToReject = pendingMembers.find((m) => m.id === memberId)

    if (memberToReject) {
      // 신청 인원 목록에서 제거
      setPendingMembers((prev) => prev.filter((m) => m.id !== memberId))

      toast({
        title: "회원 신청 거절",
        description: `${memberToReject.name}님의 회원 신청이 거절되었습니다.`,
        duration: 3000,
      })
    }
  }

  return (
    <>
      <Header title="회원 목록" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16">
        {/* Summary Card */}
        <Card>
          <CardContent className="p-4">
            <div className="flex justify-between items-center">
              <div className="text-sm text-gray-600">이번 달 회비 납부 현황</div>
              <div className="text-sm font-medium">
                {members.filter((m) => m.monthlyFee.status === "완료").length}/{members.length}명 완료
              </div>
            </div>
          </CardContent>
        </Card>

        {/* 초대 링크 버튼 (총무만 볼 수 있음) */}
        {isManager && (
          <Button className="w-full bg-blue-500 hover:bg-blue-600" onClick={generateInviteLink}>
            <Link className="h-4 w-4 mr-2" />
            초대 링크 생성하기
          </Button>
        )}

        {/* 신청 인원 섹션 (총무만 볼 수 있음) */}
        {isManager && pendingMembers.length > 0 && (
          <div className="space-y-3">
            <div className="flex justify-between items-center">
              <h2 className="text-lg font-semibold flex items-center">
                <UserPlus className="h-5 w-5 mr-2 text-blue-500" />
                신청 인원
                <Badge className="ml-2 bg-blue-100 text-blue-800">{pendingMembers.length}</Badge>
              </h2>
            </div>

            {pendingMembers.map((member) => (
              <Card key={member.id} className="hover:shadow-md transition-shadow">
                <CardContent className="p-4">
                  <div className="flex items-start justify-between">
                    <div className="flex items-center">
                      <Avatar className="h-10 w-10">
                        <AvatarImage src={member.avatar} />
                        <AvatarFallback>{member.name.slice(0, 2)}</AvatarFallback>
                      </Avatar>
                      <div className="ml-3">
                        <div className="flex items-center">
                          <span className="font-medium">{member.name}</span>
                        </div>
                        <div className="text-sm text-gray-500 mt-1">{member.email}</div>
                        <div className="text-xs text-gray-400 mt-1">신청일: {member.requestDate}</div>
                      </div>
                    </div>
                    <div className="flex space-x-2">
                      <Button
                        size="sm"
                        variant="outline"
                        className="text-green-600 border-green-200 hover:bg-green-50"
                        onClick={() => handleAccept(member.id)}
                      >
                        <Check className="h-4 w-4 mr-1" />
                        수락
                      </Button>
                      <Button
                        size="sm"
                        variant="outline"
                        className="text-red-600 border-red-200 hover:bg-red-50"
                        onClick={() => handleReject(member.id)}
                      >
                        <X className="h-4 w-4 mr-1" />
                        거절
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}

        {/* Members List */}
        <div className="space-y-3">
          <h2 className="text-lg font-semibold">회원 목록</h2>
          {members.map((member) => (
            <Card
              key={member.id}
              className={`hover:shadow-md transition-shadow ${isManager && !member.isManager ? "cursor-pointer" : ""}`}
              onClick={() => handleMemberClick(member)}
            >
              <CardContent className="p-4">
                <div className="flex items-start justify-between">
                  <div className="flex items-center">
                    <Avatar className="h-10 w-10">
                      <AvatarImage src={member.avatar} />
                      <AvatarFallback>{member.name.slice(0, 2)}</AvatarFallback>
                    </Avatar>
                    <div className="ml-3">
                      <div className="flex items-center">
                        <span className="font-medium">{member.name}</span>
                        {member.isManager && (
                          <Badge variant="secondary" className="ml-2">
                            <Shield className="h-3 w-3 mr-1" />
                            총무
                          </Badge>
                        )}
                      </div>
                      <div className="text-sm text-gray-500 mt-1">잔액: {member.balance.toLocaleString()}원</div>
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="text-sm text-gray-600">보증금: {member.deposit.toLocaleString()}원</div>
                    <div className="mt-1">
                      <Badge
                        variant={member.monthlyFee.status === "완료" ? "outline" : "destructive"}
                        className={
                          member.monthlyFee.status === "완료" ? "text-green-600" : "bg-red-100 text-red-800 border-0"
                        }
                      >
                        {member.monthlyFee.status === "미납" && <AlertCircle className="h-3 w-3 mr-1" />}
                        {member.monthlyFee.status}
                      </Badge>
                    </div>
                    {isManager && !member.isManager && selectedMember?.id === member.id && (
                      <Button
                        size="sm"
                        variant="outline"
                        className="mt-2 text-yellow-600 border-yellow-200 hover:bg-yellow-50"
                        onClick={(e) => {
                          e.stopPropagation()
                          setIsTransferDialogOpen(true)
                        }}
                      >
                        <Crown className="h-4 w-4 mr-1" />
                        총무 위임
                      </Button>
                    )}
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </main>

      {/* 초대 링크 다이얼로그 */}
      <Dialog open={isInviteDialogOpen} onOpenChange={setIsInviteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>초대 링크</DialogTitle>
            <DialogDescription>아래 링크를 공유하여 모임에 새 회원을 초대하세요.</DialogDescription>
          </DialogHeader>
          <div className="flex items-center space-x-2">
            <Input value={inviteLink} readOnly className="flex-1" />
            <Button variant="outline" onClick={copyInviteLink}>
              <Copy className="h-4 w-4" />
            </Button>
          </div>
          <DialogFooter>
            <Button className="w-full" onClick={shareInviteLink}>
              <Share2 className="h-4 w-4 mr-2" />
              공유하기
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 총무 위임 확인 다이얼로그 */}
      <Dialog open={isTransferDialogOpen} onOpenChange={setIsTransferDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>총무 위임</DialogTitle>
            <DialogDescription>{selectedMember?.name}님에게 총무를 위임하시겠습니까?</DialogDescription>
          </DialogHeader>
          <div className="py-4">
            <p className="text-sm text-gray-500">
              총무를 위임하면 모임 관리 권한이 이전되며, 이 작업은 되돌릴 수 없습니다.
            </p>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsTransferDialogOpen(false)}>
              취소
            </Button>
            <Button onClick={handleTransferConfirm}>확인</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 비밀번호 입력 다이얼로그 */}
      <Dialog open={isPasswordDialogOpen} onOpenChange={setIsPasswordDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>비밀번호 확인</DialogTitle>
            <DialogDescription>총무 위임을 위해 비밀번호를 입력해주세요.</DialogDescription>
          </DialogHeader>
          <div className="py-4">
            <Input
              type="password"
              placeholder="비밀번호"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsPasswordDialogOpen(false)}>
              취소
            </Button>
            <Button onClick={handlePasswordConfirm} disabled={!password}>
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
            <Button onClick={handlePinConfirm} className="w-full" disabled={pinCode.length !== 6 || isProcessing}>
              {isProcessing ? "처리 중..." : "확인"}
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </>
  )
}

