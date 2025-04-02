"use client"
// 총무가 보는 페이지지
import { useState, use, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Shield, AlertCircle, Check, X, UserPlus, Link, Copy, Share2, Crown } from "lucide-react"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
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
import { publicApi } from "@/lib/api"

interface Member {
  name: string;
  email: string;
  createdAt: string;
  balance: number;
  gatheringPaymentStatus: boolean;
}

interface InviteMember extends Member {
  id: number;
}

interface Manager extends Member {
  // manager는 Member와 동일한 구조를 가짐
}

interface GroupMembersResponse {
  inviteList: InviteMember[];
  manager: Manager;
  memberList: Member[];
}

export default function GroupMembersPage({ params }: { params: Promise<{ groupId: string }> }) {
  const router = useRouter()
  const {groupId} = use(params)

  // 현재 사용자가 총무인지 여부
  const [isManager, setIsManager] = useState(false)
  const [members, setMembers] = useState<Member[]>([])
  const [pendingMembers, setPendingMembers] = useState<InviteMember[]>([])
  const [manager, setManager] = useState<Manager | null>(null)
  const [isLoading, setIsLoading] = useState(true)

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
  
  // API 호출 함수
  const fetchGroupMembers = async () => {
    try {
      setIsLoading(true)
      const response = await publicApi.get<GroupMembersResponse>(`api/v1/gathering/${groupId}/member-manage`) as unknown as GroupMembersResponse
      console.log(response)
      setMembers(response.memberList)
      setPendingMembers(response.inviteList)
      setManager(response.manager)
      
      // 현재 사용자가 총무인지 확인 (이메일로 비교)
      const currentUserEmail = "test1@naver.com" // 실제로는 로그인된 사용자의 이메일을 사용해야 함
      setIsManager(response.manager.email === currentUserEmail)
    } catch (error) {
      console.error('Error fetching group members:', error)
      toast({
        title: "데이터 로딩 실패",
        description: "회원 정보를 불러오는데 실패했습니다.",
        variant: "destructive",
      })
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    fetchGroupMembers()
  }, [groupId])

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
  const handleMemberClick = (member: Member) => {
    if (isManager && !member.gatheringPaymentStatus) {
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
          if (member.email === manager?.email) {
            return { ...member, gatheringPaymentStatus: false }
          }
          if (member.email === selectedMember.email) {
            return { ...member, gatheringPaymentStatus: true }
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
  const handleAccept = async (member: InviteMember) => {
    try {
      // API 호출로 처리
      await publicApi.post(`api/v1/gathering/${groupId}/members/${member.id}/accept`)
      
      // 신청 인원 목록에서 제거
      setPendingMembers((prev) => prev.filter((m) => m.id !== member.id))

      // 회원 목록에 추가
      setMembers((prev) => [
        ...prev,
        {
          name: member.name,
          email: member.email,
          createdAt: member.createdAt,
          balance: 0,
          gatheringPaymentStatus: false,
        },
      ])

      toast({
        title: "회원 신청 수락",
        description: `${member.name}님의 회원 신청이 수락되었습니다.`,
        duration: 3000,
      })
    } catch (error) {
      toast({
        title: "회원 신청 수락 실패",
        description: "회원 신청 수락 중 오류가 발생했습니다.",
        variant: "destructive",
      })
    }
  }

  // 신청 거절 함수
  const handleReject = async (member: InviteMember) => {
    try {
      // API 호출로 처리
      await publicApi.post(`api/v1/gathering/${groupId}/members/${member.id}/reject`)
      
      // 신청 인원 목록에서 제거
      setPendingMembers((prev) => prev.filter((m) => m.id !== member.id))

      toast({
        title: "회원 신청 거절",
        description: `${member.name}님의 회원 신청이 거절되었습니다.`,
        duration: 3000,
      })
    } catch (error) {
      toast({
        title: "회원 신청 거절 실패",
        description: "회원 신청 거절 중 오류가 발생했습니다.",
        variant: "destructive",
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
                {isLoading ? (
                  <span className="text-gray-400">로딩 중...</span>
                ) : (
                  `${members.filter((m) => m.gatheringPaymentStatus).length}/${members.length}명 완료`
                )}
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
                        <AvatarFallback>{member.name.slice(0, 2)}</AvatarFallback>
                      </Avatar>
                      <div className="ml-3">
                        <div className="flex items-center">
                          <span className="font-medium">{member.name}</span>
                        </div>
                        <div className="text-sm text-gray-500 mt-1">{member.email}</div>
                        <div className="text-xs text-gray-400 mt-1">신청일: {new Date(member.createdAt).toLocaleDateString()}</div>
                      </div>
                    </div>
                    <div className="flex space-x-2">
                      <Button
                        size="sm"
                        variant="outline"
                        className="text-green-600 border-green-200 hover:bg-green-50"
                        onClick={() => handleAccept(member)}
                      >
                        <Check className="h-4 w-4 mr-1" />
                        수락
                      </Button>
                      <Button
                        size="sm"
                        variant="outline"
                        className="text-red-600 border-red-200 hover:bg-red-50"
                        onClick={() => handleReject(member)}
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
          
          {/* 총무 정보 */}
          {manager && (
            <Card className="hover:shadow-md transition-shadow border-blue-100">
              <CardContent className="p-4">
                <div className="flex items-start justify-between">
                  <div className="flex items-center">
                    <Avatar className="h-10 w-10 bg-blue-100">
                      <AvatarFallback className="text-blue-600">{manager.name.slice(0, 2)}</AvatarFallback>
                    </Avatar>
                    <div className="ml-3">
                      <div className="flex items-center">
                        <span className="font-medium">{manager.name}</span>
                        <Badge variant="secondary" className="ml-2 bg-blue-100 text-blue-800 border-0">
                          <Crown className="h-3 w-3 mr-1" />
                          총무
                        </Badge>
                      </div>
                      <div className="text-sm text-gray-500 mt-1">잔액: {manager.balance.toLocaleString()}원</div>
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="mt-1">
                      <Badge
                        variant={manager.gatheringPaymentStatus ? "outline" : "destructive"}
                        className={
                          manager.gatheringPaymentStatus ? "text-green-600" : "bg-red-100 text-red-800 border-0"
                        }
                      >
                        {!manager.gatheringPaymentStatus && <AlertCircle className="h-3 w-3 mr-1" />}
                        {manager.gatheringPaymentStatus ? "완료" : "미납"}
                      </Badge>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          {/* 일반 회원 목록 */}
          {members.map((member, index) => (
            <Card
              key={index}
              className="hover:shadow-md transition-shadow"
            >
              <CardContent className="p-4">
                <div className="flex items-start justify-between">
                  <div className="flex items-center">
                    <Avatar className="h-10 w-10">
                      <AvatarFallback>{member.name.slice(0, 2)}</AvatarFallback>
                    </Avatar>
                    <div className="ml-3">
                      <div className="flex items-center">
                        <span className="font-medium">{member.name}</span>
                      </div>
                      <div className="text-sm text-gray-500 mt-1">잔액: {member.balance.toLocaleString()}원</div>
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="mt-1">
                      <Badge
                        variant={member.gatheringPaymentStatus ? "outline" : "destructive"}
                        className={
                          member.gatheringPaymentStatus ? "text-green-600" : "bg-red-100 text-red-800 border-0"
                        }
                      >
                        {!member.gatheringPaymentStatus && <AlertCircle className="h-3 w-3 mr-1" />}
                        {member.gatheringPaymentStatus ? "완료" : "미납"}
                      </Badge>
                    </div>
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

