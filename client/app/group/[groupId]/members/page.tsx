"use client"
// 총무가 보는 페이지지
import { useState, use, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Shield, AlertCircle, Check, X, UserPlus, Link, Copy, Share2, Crown, Lock, Trash2, XCircle, RefreshCw, Users } from "lucide-react"
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
import { publicApi, authApi } from "@/lib/api"

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
  isManager: boolean;
  myDeposit: number;
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
  const [myDeposit, setMyDeposit] = useState<number>(0)
  
  // API 호출 함수
  const fetchGroupMembers = async () => {
    try {
      setIsLoading(true)
      const response = await authApi.get<GroupMembersResponse>(`api/v1/gathering/${groupId}/member-manage`) as unknown as GroupMembersResponse
      console.log(response)
      setMembers(response.memberList)
      setPendingMembers(response.inviteList)
      setManager(response.manager)
      
      // API 응답의 isManager 값을 직접 사용
      setIsManager(response.isManager)
      setMyDeposit(response.myDeposit)
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
      await authApi.post(`api/v1/gathering/${groupId}/members/${member.id}/accept`)
      
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
      await authApi.post(`api/v1/gathering/${groupId}/members/${member.id}/reject`)
      
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

  // 회원 상태 체크 함수
  const checkPaymentStatus = (balance: number) => {
    return balance >= 0
  }

  return (
    <>
      <Header title="회원 목록" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16 bg-slate-50">
        {/* 보증금 납부 현황 카드 */}
        <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
          <CardContent className="p-5">
            <div className="flex justify-between items-center">
              <div className="flex items-center text-slate-700">
                <Shield className="h-5 w-5 mr-2 text-blue-600" />
                <span className="font-medium">보증금 납부 현황</span>
              </div>
              <Badge variant="outline" className="bg-blue-50 text-blue-600 border-blue-200 px-3 py-1">
                {isLoading ? (
                  <div className="flex items-center">
                    <RefreshCw className="h-3 w-3 mr-1 animate-spin" />
                    로딩 중...
                  </div>
                ) : (
                  `${members.filter((m) => checkPaymentStatus(m.balance)).length + (manager && checkPaymentStatus(manager.balance) ? 1 : 0)}/${members.length + 1}명 완료`
                )}
              </Badge>
            </div>
          </CardContent>
        </Card>

        {/* 초대 링크 버튼 */}
        {isManager && (
          <Button 
            className="w-full py-6 rounded-xl bg-blue-600 hover:bg-blue-700 transition-colors"
            onClick={generateInviteLink}
          >
            <Link className="h-5 w-5 mr-2" />
            초대 링크 생성하기
          </Button>
        )}

        {/* 신청 인원 섹션 */}
        {isManager && pendingMembers.length > 0 && (
          <div className="space-y-3">
            <div className="flex justify-between items-center">
              <h2 className="text-lg font-semibold text-slate-800 flex items-center">
                <UserPlus className="h-5 w-5 mr-2 text-blue-600" />
                신청 인원
                <Badge className="ml-2 bg-blue-50 text-blue-600 border-blue-200">
                  {pendingMembers.length}
                </Badge>
              </h2>
            </div>

            {pendingMembers.map((member) => (
              <Card 
                key={member.id} 
                className="border-0 shadow-sm rounded-xl overflow-hidden hover:shadow-md transition-all duration-200 transform hover:-translate-y-0.5"
              >
                <CardContent className="p-5">
                  <div className="flex items-start justify-between">
                    <div className="flex items-center">
                      <Avatar className="h-12 w-12 border-2 border-blue-100">
                        <AvatarFallback className="bg-blue-50 text-blue-600">
                          {member.name.slice(0, 2)}
                        </AvatarFallback>
                      </Avatar>
                      <div className="ml-4">
                        <div className="flex items-center">
                          <span className="font-medium text-slate-800">{member.name}</span>
                        </div>
                        <div className="text-sm text-slate-500 mt-1">{member.email}</div>
                        <div className="text-xs text-slate-400 mt-1">
                          신청일: {new Date(member.createdAt).toLocaleDateString()}
                        </div>
                      </div>
                    </div>
                    <div className="flex space-x-2">
                      <Button
                        size="sm"
                        variant="outline"
                        className="rounded-lg text-green-600 border-green-200 hover:bg-green-50"
                        onClick={() => handleAccept(member)}
                      >
                        <Check className="h-4 w-4 mr-1" />
                        수락
                      </Button>
                      <Button
                        size="sm"
                        variant="outline"
                        className="rounded-lg text-red-600 border-red-200 hover:bg-red-50"
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

        {/* 회원 목록 */}
        <div className="space-y-3">
          <h2 className="text-lg font-semibold text-slate-800 flex items-center">
            <Users className="h-5 w-5 mr-2 text-blue-600" />
            회원 목록
          </h2>
          
          {/* 총무 정보 */}
          {manager && (
            <Card className="border-0 shadow-sm rounded-xl overflow-hidden bg-gradient-to-br from-blue-50 to-white">
              <CardContent className="p-5">
                <div className="flex items-start justify-between">
                  <div className="flex items-center">
                    <Avatar className="h-12 w-12 border-2 border-blue-200">
                      <AvatarFallback className="bg-blue-100 text-blue-600">
                        {manager.name.slice(0, 2)}
                      </AvatarFallback>
                    </Avatar>
                    <div className="ml-4">
                      <div className="flex items-center">
                        <span className="font-medium text-slate-800">{manager.name}</span>
                        <Badge className="ml-2 bg-blue-100 text-blue-700 border-0">
                          <Crown className="h-3 w-3 mr-1" />
                          총무
                        </Badge>
                      </div>
                      <div className="text-sm text-slate-600 mt-1">
                        잔액: {manager.balance < 0 ? 0 : manager.balance.toLocaleString()}원
                      </div>
                    </div>
                  </div>
                  <Badge
                    variant={manager.balance >= 0 ? "outline" : "destructive"}
                    className={
                      manager.balance >= 0
                        ? "bg-green-50 text-green-600 border-green-200"
                        : "bg-red-50 text-red-600 border-red-200"
                    }
                  >
                    {manager.balance < 0 && <AlertCircle className="h-3 w-3 mr-1" />}
                    {manager.balance >= 0 ? "완료" : "미납"}
                  </Badge>
                </div>
              </CardContent>
            </Card>
          )}

          {/* 일반 회원 목록 */}
          {members.map((member, index) => (
            <Card
              key={index}
              className="border-0 shadow-sm rounded-xl overflow-hidden hover:shadow-md transition-all duration-200 transform hover:-translate-y-0.5"
            >
              <CardContent className="p-5">
                <div className="flex items-start justify-between">
                  <div className="flex items-center">
                    <Avatar className="h-12 w-12 border-2 border-slate-100">
                      <AvatarFallback className="bg-slate-50 text-slate-600">
                        {member.name.slice(0, 2)}
                      </AvatarFallback>
                    </Avatar>
                    <div className="ml-4">
                      <div className="flex items-center">
                        <span className="font-medium text-slate-800">{member.name}</span>
                      </div>
                      <div className="text-sm text-slate-600 mt-1">
                        잔액: {member.balance < 0 ? 0 : member.balance.toLocaleString()}원
                      </div>
                    </div>
                  </div>
                  <Badge
                    variant={member.balance >= 0 ? "outline" : "destructive"}
                    className={
                      member.balance >= 0
                        ? "bg-green-50 text-green-600 border-green-200"
                        : "bg-red-50 text-red-600 border-red-200"
                    }
                  >
                    {member.balance < 0 && <AlertCircle className="h-3 w-3 mr-1" />}
                    {member.balance >= 0 ? "완료" : "미납"}
                  </Badge>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </main>

      {/* 초대 링크 다이얼로그 */}
      <Dialog open={isInviteDialogOpen} onOpenChange={setIsInviteDialogOpen}>
        <DialogContent className="sm:max-w-md rounded-xl">
          <DialogHeader>
            <DialogTitle className="flex items-center text-slate-800">
              <Link className="h-5 w-5 mr-2 text-blue-600" />
              초대 링크
            </DialogTitle>
            <DialogDescription>
              아래 링크를 공유하여 모임에 새 회원을 초대하세요.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div className="flex items-center space-x-2">
              <Input 
                value={inviteLink} 
                readOnly 
                className="flex-1 py-5 rounded-xl border-slate-200"
              />
              <Button 
                variant="outline" 
                onClick={copyInviteLink}
                className="rounded-xl border-slate-200 hover:bg-slate-50"
              >
                <Copy className="h-4 w-4" />
              </Button>
            </div>
            <Button 
              className="w-full py-6 rounded-xl bg-blue-600 hover:bg-blue-700"
              onClick={shareInviteLink}
            >
              <Share2 className="h-4 w-4 mr-2" />
              공유하기
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      {/* 총무 위임 확인 다이얼로그 */}
      <Dialog open={isTransferDialogOpen} onOpenChange={setIsTransferDialogOpen}>
        <DialogContent className="sm:max-w-md rounded-xl">
          <DialogHeader>
            <DialogTitle className="flex items-center text-slate-800">
              <Crown className="h-5 w-5 mr-2 text-blue-600" />
              총무 위임
            </DialogTitle>
            <DialogDescription>
              {selectedMember?.name}님에게 총무를 위임하시겠습니까?
            </DialogDescription>
          </DialogHeader>
          <div className="p-4 bg-blue-50 rounded-xl border border-blue-100">
            <AlertCircle className="h-5 w-5 text-blue-600 mb-2" />
            <p className="text-sm text-blue-700">
              총무를 위임하면 모임 관리 권한이 이전되며, 이 작업은 되돌릴 수 없습니다.
            </p>
          </div>
          <DialogFooter className="gap-2 sm:gap-0">
            <Button 
              variant="outline" 
              onClick={() => setIsTransferDialogOpen(false)}
              className="rounded-xl border-slate-200"
            >
              취소
            </Button>
            <Button 
              onClick={handleTransferConfirm}
              className="rounded-xl bg-blue-600 hover:bg-blue-700"
            >
              확인
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 비밀번호 입력 다이얼로그 */}
      <Dialog open={isPasswordDialogOpen} onOpenChange={setIsPasswordDialogOpen}>
        <DialogContent className="sm:max-w-md rounded-xl">
          <DialogHeader>
            <DialogTitle className="flex items-center text-slate-800">
              <Lock className="h-5 w-5 mr-2 text-blue-600" />
              비밀번호 확인
            </DialogTitle>
            <DialogDescription>
              총무 위임을 위해 비밀번호를 입력해주세요.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div className="relative">
              <Input
                type="password"
                placeholder="비밀번호"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="pl-10 py-6 rounded-xl border-slate-200"
              />
              <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                <Lock className="h-5 w-5 text-slate-400" />
              </div>
            </div>
          </div>
          <DialogFooter className="gap-2 sm:gap-0">
            <Button 
              variant="outline" 
              onClick={() => setIsPasswordDialogOpen(false)}
              className="rounded-xl border-slate-200"
            >
              취소
            </Button>
            <Button 
              onClick={handlePasswordConfirm} 
              disabled={!password}
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
                총무 위임을 완료하려면 6자리 PIN 번호를 입력해주세요.
              </DialogDescription>
            </div>
          </DialogHeader>
          <div className="space-y-5">
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
              onClick={handlePinConfirm} 
              className="w-full py-6 rounded-xl bg-blue-600 hover:bg-blue-700"
              disabled={pinCode.length !== 6 || isProcessing}
            >
              {isProcessing ? (
                <div className="flex items-center">
                  <RefreshCw className="h-4 w-4 mr-2 animate-spin" />
                  처리 중...
                </div>
              ) : (
                <div className="flex items-center">
                  <Check className="h-5 w-5 mr-2" />
                  확인
                </div>
              )}
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </>
  )
}

