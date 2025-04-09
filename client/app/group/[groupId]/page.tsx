"use client";
import { useRouter } from "next/navigation";
import {
  Plus,
  User,
  Clock,
  Settings,
  CalendarIcon,
  Users,
  Wallet,
  AlertCircle,
  MapPin,
  CoinsIcon,
  Coins,
  DollarSign,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Header } from "@/components/Header";
import React, { use, useState, useEffect } from "react";
import { toast } from "@/components/ui/use-toast";
import { publicApi, authApi } from "@/lib/api";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";

type Manager = {
  name: string;
  avatar: string;
};

type Accounts = {
  groupBalance: number;
  groupDeposit: number;
  myBalance: number;
  myDeposit: number;
};

type Schedule = {
  id: number;
  date: string;
  participants: number;
  budgetPerPerson: number;
  totalBudget: number;
  location: string;
  isChecked: boolean;
  isAttend: boolean;
  name: string;
  subManager : boolean;
  scheduleAccountBalance : number;
};

type GroupData = {
  id: number;
  name: string;
  description: string;
  totalMembers: number;
  monthlyFee: number;
  isManager: boolean;
  manager: Manager;
  accounts: Accounts;
  schedules: Schedule[];
};

export default function GroupDetailPage({
  params,
}: {
  params: Promise<{ groupId: string }>;
}) {
  const router = useRouter();
  const { groupId } = use(params);
  const [isLeaveDialogOpen, setIsLeaveDialogOpen] = useState(false);
  const [groupData, setGroupData] = useState<GroupData | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // 그룹 상세 정보 가져오기
  const fetchGroupDetail = async () => {
    try {
      setIsLoading(true);
      const response = (await authApi.get<GroupData>(
        `api/v1/gathering/${groupId}/detail`
      )) as unknown as GroupData;
      console.log(response);
      response.id = parseInt(groupId);
      setGroupData(response);
      console.log(response.schedules)
    } catch (error) {
      console.error("그룹 상세 정보를 가져오는데 실패했습니다:", error);
      toast({
        title: "데이터 로딩 실패",
        description: "그룹 상세 정보를 가져오는데 실패했습니다.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchGroupDetail();
  }, [groupId]);

  if (isLoading || !groupData) {
    return (
      <>
        <Header title="로딩 중..." showBackButton />
        <main className="flex-1 overflow-auto p-4 bg-slate-50">
          <div className="flex items-center justify-center h-full">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          </div>
        </main>
      </>
    );
  }

  // 모임 탈퇴 처리 함수
  const handleLeaveGroup = async () => {
    try {
      await authApi.delete(`api/v1/gathering/${groupId}/leave`);

      toast({
        title: "모임 탈퇴 완료",
        description: "모임에서 탈퇴되었습니다.",
      });

      router.push("/"); // 메인 페이지로 이동
    } catch (error) {
      toast({
        title: "오류 발생",
        description: "탈퇴 처리 중 오류가 발생했습니다. 다시 시도해주세요.",
        variant: "destructive",
      });
    }
    setIsLeaveDialogOpen(false);
  };

  // 거절 처리 함수 추가
  const handleCancel = async (scheduleId: number, e: React.MouseEvent) => {
    e.stopPropagation(); // 카드 클릭 이벤트 전파 방지
    try {
      await authApi.post(`api/v1/schedule/${scheduleId}/attend-reject`);

      // 성공 시 해당 일정의 상태 업데이트
      setGroupData((prev) => {
        if (!prev) return prev;
        return {
          ...prev,
          schedules: prev.schedules.map((schedule) => {
            if (schedule.id === scheduleId) {
              return {
                ...schedule,
                isChecked: true,
                isAttend: false,
              };
            }
            return schedule;
          }),
        };
      });

      toast({
        title: "거절 완료",
        description: "일정 거절이 완료되었습니다.",
      });
    } catch (error) {
      toast({
        title: "거절 실패",
        description: "알 수 없는 오류가 발생했습니다.",
        variant: "destructive",
      });
    }
  };


  
  // 참석 처리 함수 추가
  const handleAttend = async (scheduleId: number, e: React.MouseEvent) => {
    e.stopPropagation(); // 카드 클릭 이벤트 전파 방지
    try {
      await authApi.post(`api/v1/schedule/${scheduleId}/attend`);
      
      // 성공 시 해당 일정의 상태 업데이트
      setGroupData((prev) => {
        if (!prev) return prev;
        return {
          ...prev,
          schedules: prev.schedules.map((schedule) => {
            if (schedule.id === scheduleId) {
              return {
                ...schedule,
                isChecked: true,
                isAttend: true,
              };
            }
            return schedule;
          }),
        };
      });
      
      toast({
        title: "참석 완료",
        description: "일정 참석이 완료되었습니다.",
      });
    } catch (error) {
      toast({
        title: "참석 실패",
        description: "알 수 없는 오류가 발생했습니다.",
        variant: "destructive",
      });
    }
  };
  
  // 참석 취소 처리 함수 추가
  const handleCancelAttend = async (
    scheduleId: number,
    e: React.MouseEvent
  ) => {
    e.stopPropagation(); // 카드 클릭 이벤트 전파 방지
    try {
      await authApi.post(`api/v1/schedule/${scheduleId}/attend-cancel`);
      
      // 성공 시 해당 일정의 상태 업데이트
      setGroupData((prev) => {
        if (!prev) return prev;
        return {
          ...prev,
          schedules: prev.schedules.map((schedule) => {
            if (schedule.id === scheduleId) {
              return {
                ...schedule,
                isChecked: false,
                isAttend: false,
              };
            }
            return schedule;
          }),
        };
      });
      
      toast({
        title: "참석 취소 완료",
        description: "일정 참석이 취소되었습니다.",
      });
    } catch (error) {
      toast({
        title: "참석 취소 실패",
        description: "알 수 없는 오류가 발생했습니다.",
        variant: "destructive",
      });
    }
  };
  
  return (
    <>
      <Header title={groupData.name} showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-20 bg-slate-50">
        {/* 그룹 정보 카드 */}
        <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
          <CardContent className="p-6">
            <div className="flex items-start justify-between mb-6">
              <div className="space-y-4">
                <div>
                  <h2 className="text-xl font-semibold text-slate-800">{groupData.name}</h2>
                  <p className="text-sm text-slate-500 mt-1">{groupData.description}</p>
                </div>
              </div>

              <div className="flex items-center gap-3 bg-slate-50 p-3 rounded-lg">
                <div>
                  <div className="text-sm text-slate-600 flex items-center">
                    <User className="h-3.5 w-3.5 mr-1" />
                    <span className="font-medium">총무</span>
                  </div>
                  <div className="text-sm font-medium text-slate-800 text-right">
                    {groupData.manager.name}
                  </div>
                </div>
              </div>
            </div>

            <div className="space-y-2">
              {groupData.isManager && (
                <Button
                  variant="outline"
                  className="w-full py-6 rounded-xl border-slate-200 hover:bg-slate-50 text-slate-700"
                  onClick={() => router.push(`/group/${groupId}/members`)}
                >
                  <Settings className="h-4 w-4 mr-2" />
                  회원 관리
                </Button>
              )}
              <Button
                variant="outline"
                className="w-full py-6 rounded-xl border-slate-200 hover:bg-slate-50 text-slate-700"
                onClick={() => router.push(`/group/${groupId}/member-list`)}
              >
                <Users className="h-4 w-4 mr-2" />
                회원 목록
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* 계좌 정보 그리드 */}
        <div className="grid grid-cols-3 gap-3">
          <Card className="border-0 shadow-sm rounded-xl overflow-hidden bg-gradient-to-br from-blue-50 to-white">
            <CardContent className="p-4">
              <div className="text-xs text-slate-600 mb-1.5">모임 보증금</div>
              <div className="font-semibold text-blue-600 flex items-center">
                <Wallet className="h-4 w-4 mr-1.5" />
                {groupData.accounts.groupDeposit.toLocaleString()}원
              </div>
            </CardContent>
          </Card>
          
          <Card className="border-0 shadow-sm rounded-xl overflow-hidden bg-gradient-to-br from-green-50 to-white">
            <CardContent className="p-4">
              <div className="text-xs text-slate-600 mb-1.5">나의 가용 금액</div>
              <div className="font-semibold text-green-600 flex items-center">
                <DollarSign className="h-4 w-4 mr-1.5" />

                {groupData.accounts.myBalance.toLocaleString()}원
              </div>
            </CardContent>
          </Card>
          
          <Card className="border-0 shadow-sm rounded-xl overflow-hidden bg-gradient-to-br from-slate-50 to-white">
            <CardContent className="p-4">
              <div className="text-xs text-slate-600 mb-1.5">내 보증금</div>
              <div className="font-semibold text-slate-700 flex items-center">
                <Coins className="h-4 w-4 mr-1.5" />
                {groupData.accounts.myDeposit.toLocaleString()}원
              </div>
            </CardContent>
          </Card>
        </div>

        <Button
          variant="outline"
          className="w-full py-6 rounded-xl border-slate-200 hover:bg-slate-50 text-slate-700"
          onClick={() => router.push(`/group/${groupId}/account-history`)}
        >
          <Wallet className="h-4 w-4 mr-2" />
          모임통장 내역 보기
        </Button>

        {/* 월간 상태 */}
        <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
          <CardContent className="p-5">
            <div className="flex justify-between items-center">
              <div className="flex items-center text-slate-700">
                <CoinsIcon className="h-4 w-4 mr-2" />
                <span className="font-medium">이번 달 모임비</span>
              </div>
              <Badge variant="outline" className="bg-green-50 text-green-600 border-green-200 px-3 py-1 rounded-lg">
                {groupData.monthlyFee.toLocaleString()}원
              </Badge>
            </div>
          </CardContent>
        </Card>

        {/* 일정 섹션 */}
        <div className="space-y-3">
          <div className="flex justify-between items-center">
            <h2 className="text-lg font-semibold text-slate-800 flex items-center">
              <CalendarIcon className="h-5 w-5 mr-2 text-blue-600" />
              일정
            </h2>
            <Button
              onClick={() => router.push(`/group/${groupId}/create-schedule`)}
              className="bg-blue-600 hover:bg-blue-700 rounded-xl"
            >
              <Plus className="h-4 w-4 mr-2" />
              새 일정 만들기
            </Button>
          </div>


          <div className="space-y-3">
            {groupData.schedules.map((schedule) => (
              <Card
                key={schedule.id}
                className="border-0 shadow-sm rounded-xl overflow-hidden hover:shadow-md transition-all duration-200 transform hover:-translate-y-0.5"
                onClick={() => router.push(`/group/${groupId}/schedule/${schedule.id}`)}
              >
                <CardContent className="p-5">
                  <div className="flex justify-between items-start mb-4">
                    <div className="space-y-1">
                      <div className="font-medium text-slate-800">{schedule.name}</div>
                      <Badge variant="outline" className="bg-blue-50 text-blue-600 border-blue-200">
                        {schedule.participants}명 참여
                      </Badge>
                    </div>
                    
                    <div className="flex items-center space-x-2">
                      {schedule.subManager ? null : schedule.isAttend ? (
                        <Button
                          size="sm"
                          variant="outline"
                          className="rounded-lg text-red-600 border-red-200 hover:bg-red-50"

                          onClick={(e) => handleCancelAttend(schedule.id, e)}
                        >
                          참석 취소
                        </Button>
                      ) : (
                        <>
                          <Button
                            size="sm"
                            variant="outline"
                            className="rounded-lg text-green-600 border-green-200 hover:bg-green-50"

                            onClick={(e) => handleAttend(schedule.id, e)}
                          >
                            참석
                          </Button>
                          <Button
                            size="sm"
                            variant="outline"
                            className="rounded-lg text-red-600 border-red-200 hover:bg-red-50"
                            onClick={(e) => handleCancel(schedule.id, e)}
                          >
                            거절
                          </Button>
                        </>
                      )}
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-3">
                    <div className="flex items-center text-sm text-slate-600">
                      <CalendarIcon className="h-4 w-4 mr-2 text-slate-400" />
                      {new Date(schedule.date).toLocaleDateString()}
                    </div>
                    <div className="flex items-center text-sm text-slate-600">
                      <Clock className="h-4 w-4 mr-2 text-slate-400" />
                      {new Date(schedule.date).toLocaleTimeString([], {
                        hour: "2-digit",
                        minute: "2-digit",
                        hour12: true,
                      })}
                    </div>
                    <div className="flex items-center text-sm text-slate-600">
                      <MapPin className="h-4 w-4 mr-2 text-slate-400" />
                      {schedule.location}
                    </div>
                    <div className="flex items-center text-sm text-slate-600">
                      <DollarSign className="h-4 w-4 mr-2 text-slate-400" />
                      {schedule.budgetPerPerson.toLocaleString()}원/인
                    </div>
                  </div>

                  <div className="mt-3 pt-3 border-t border-slate-100">
                    <div className="flex items-center justify-between">
                      <span className="text-sm text-slate-600">일정 계좌 잔액</span>
                      <span className="font-medium text-blue-600">
                        {typeof schedule.scheduleAccountBalance === "number"
                          ? schedule.scheduleAccountBalance.toLocaleString()
                          : "0"} 원
                      </span>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>

        {/* 모임 탈퇴 버튼 */}
        <Button
          variant="outline"
          className="w-full py-6 rounded-xl border-red-200 text-red-600 hover:bg-red-50 mt-4"
          onClick={() => setIsLeaveDialogOpen(true)}
        >
          <AlertCircle className="h-4 w-4 mr-2" />
          모임 탈퇴
        </Button>
      </main>

      {/* 모임 탈퇴 확인 다이얼로그 */}
      <Dialog open={isLeaveDialogOpen} onOpenChange={setIsLeaveDialogOpen}>
        <DialogContent className="sm:max-w-md rounded-xl">
          <DialogHeader>
            <DialogTitle className="flex items-center text-red-600">
              <AlertCircle className="h-5 w-5 mr-2" />
              모임 탈퇴
            </DialogTitle>
            <DialogDescription className="text-slate-600">
              정말로 <span className="font-medium text-slate-700">{groupData.name}</span> 모임을 탈퇴하시겠습니까?
              <div className="mt-2 p-3 bg-red-50 rounded-lg border border-red-100">
                <p className="text-red-600 text-sm">
                  탈퇴 시 모임 내역과 보증금이 삭제되며, 이 작업은 되돌릴 수 없습니다.
                </p>
              </div>
            </DialogDescription>
          </DialogHeader>
          <DialogFooter className="gap-2 sm:gap-0">
            <Button
              variant="outline"
              onClick={() => setIsLeaveDialogOpen(false)}
              className="rounded-xl border-slate-200"
            >
              취소
            </Button>
            <Button 
              variant="destructive" 
              onClick={handleLeaveGroup}
              className="rounded-xl bg-red-600 hover:bg-red-700"
            >
              탈퇴하기
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}

