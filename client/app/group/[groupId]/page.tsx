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
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Header } from "@/components/Header";
import React, { use, useState, useEffect } from "react";
import { toast } from "@/components/ui/use-toast";
import { publicApi } from "@/lib/api";
import { LOCALHOST } from "@/lib/constants";
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
      const response = (await publicApi.get<GroupData>(
        `api/v1/gathering/${groupId}/detail`
      )) as unknown as GroupData;
      console.log(response);
      response.id = parseInt(groupId);
      setGroupData(response);
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
        <main className="flex-1 overflow-auto p-4">
          <Card>
            <CardContent className="p-4 text-center text-gray-500">
              로딩 중...
            </CardContent>
          </Card>
        </main>
      </>
    );
  }

  // 모임 탈퇴 처리 함수
  const handleLeaveGroup = async () => {
    try {
      await publicApi.post(LOCALHOST + `api/v1/gathering/${groupId}/leave`);

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

  return (
    <>
      {/* 여기에 */}
      <Header title={groupData.name} showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-20">
        {/* Group Info */}
        <Card>
          <CardContent className="p-4">
            <div className="flex items-start justify-between mb-6">
              {/* 그룹 이름 & 설명 */}
              <div>
                <h2 className="text-xl font-semibold">{groupData.name}</h2>
                <p className="text-sm text-gray-500 mt-1">
                  {groupData.description}
                </p>
              </div>

              {/* 총무 정보 */}
              <div className="flex items-center gap-3">
                <div className="text-right">
                  <div className="flex items-center text-sm text-gray-600">
                    <User className="h-4 w-4 mr-1" />
                    <span className="font-medium">총무</span>
                  </div>
                  <div className="text-sm text-gray-800 mt-0.5">
                    {groupData.manager.name}
                  </div>
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

        {/* Account Info */}
        <div className="grid grid-cols-3 gap-3">
          <Card>
            <CardContent className="p-3">
              <div className="text-xs text-gray-500 mb-1">모임계좌 잔액</div>
              <div className="font-semibold text-blue-600">
                {groupData.accounts.groupBalance.toLocaleString()}원
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-3">
              <div className="text-xs text-gray-500 mb-1">내 계좌 잔액</div>
              <div className="font-semibold text-green-600">
                {groupData.accounts.myBalance.toLocaleString()}원
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-3">
              <div className="text-xs text-gray-500 mb-1">보증금</div>
              <div className="font-semibold">
                {groupData.accounts.myDeposit.toLocaleString()}원
              </div>
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
                {groupData.monthlyFee.toLocaleString()}원
              </Badge>
            </div>
          </CardContent>
        </Card>

        {/* Schedules */}
        <div className="space-y-3">
          <div className="flex justify-between items-center">
            <h2 className="text-lg font-semibold">일정</h2>
            <Button
              onClick={() => router.push(`/group/${groupId}/create-schedule`)}
            >
              <Plus className="h-4 w-4 mr-2" />새 일정 만들기
            </Button>
          </div>

          {groupData.schedules.map((schedule) => (
            <Card
              key={schedule.id}
              className="hover:shadow-md transition-shadow cursor-pointer"
              onClick={() =>
                router.push(`/group/${groupId}/schedule/${schedule.id}`)
              }
            >
              <CardContent className="p-4">
                <div className="flex justify-between items-start mb-2">
                  <div className="font-medium">{schedule.location}</div>
                  <Badge variant="secondary">#{schedule.id}차</Badge>
                </div>
                <div className="grid grid-cols-2 gap-2 text-sm text-gray-600">
                  <div className="flex items-center">
                    <CalendarIcon className="h-4 w-4 mr-1" />
                    {new Date(schedule.date).toLocaleDateString()}
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
                    {new Date(schedule.date).toLocaleTimeString()}
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

      {/* 모임 탈퇴 확인 다이얼로그 */}
      <Dialog open={isLeaveDialogOpen} onOpenChange={setIsLeaveDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>모임 탈퇴</DialogTitle>
            <DialogDescription>
              정말로 {groupData.name} 모임을 탈퇴하시겠습니까? 탈퇴 시 모임
              내역과 보증금이 삭제되며, 이 작업은 되돌릴 수 없습니다.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setIsLeaveDialogOpen(false)}
            >
              취소
            </Button>
            <Button variant="destructive" onClick={handleLeaveGroup}>
              탈퇴하기
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}
