"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { 
  Plus, 
  Search, 
  ChevronRight, 
  Link, 
  Users, 
  RefreshCw, 
  UserPlus,
  Coins,
  BarChart,
  UsersRound,
  Calendar,
  AlertCircle
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Header } from "@/components/Header";
import { toast } from "@/components/ui/use-toast";
import { authApi } from "@/lib/api";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";

type Gathering = {
  gatheringId: number;
  managerId: number;
  gatheringAccountId: number;
  gatheringName: string;
  gatheringIntroduction: string;
  memberCount: number;
  penaltyRate: number;
  depositDate: string;
  basicFee: number;
  gatheringDeposit: number;
}

type ApiResponse = {
  gatherings: Gathering[];
}

export default function GroupsPage() {
  const [searchTerm, setSearchTerm] = useState("");
  const [inviteCode, setInviteCode] = useState("");
  const [isJoinDialogOpen, setIsJoinDialogOpen] = useState(false);
  const [isJoining, setIsJoining] = useState(false);
  const [groups, setGroups] = useState<Gathering[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();

  // 그룹 목록 가져오기
  const fetchGroups = async () => {
    try {
      setIsLoading(true);
      const response = await authApi.get<ApiResponse>("api/v1/gathering") as unknown as ApiResponse;
      setGroups(response.gatherings);
    } catch (error) {
      console.error("그룹 목록을 가져오는데 실패했습니다:", error);
      toast({
        title: "데이터 로딩 실패",
        description: "그룹 목록을 가져오는데 실패했습니다.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  // 초기 데이터 로드
  useEffect(() => {
    fetchGroups();
  }, []);

  const filteredGroups = groups.filter(
    (group) =>
      group.gatheringName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      group.gatheringIntroduction.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // 초대 링크로 모임 참가 함수
  const handleJoinWithInviteCode = async () => {
    if (!inviteCode.trim()) {
      toast({
        title: "초대 코드 필요",
        description: "초대 코드를 입력해주세요.",
        variant: "destructive",
        duration: 3000,
      });
      return;
    }

    setIsJoining(true);

    try {
      // API 호출로 초대 코드 검증 및 모임 참가 처리
      await authApi.post("api/v1/gathering/join", {
        inviteCode: inviteCode,
      });

      // 성공 시 처리
      toast({
        title: "모임 참가 신청 완료",
        description: "모임 참가 신청이 완료되었습니다. 총무의 승인을 기다려주세요.",
        duration: 3000,
      });
      setIsJoinDialogOpen(false);
      setInviteCode("");
      fetchGroups(); // 그룹 목록 새로고침
    } catch (error) {
      toast({
        title: "모임 참가 실패",
        description: "유효하지 않은 초대 코드입니다. 다시 확인해주세요.",
        variant: "destructive",
        duration: 3000,
      });
    } finally {
      setIsJoining(false);
    }
  };

  // 색상 배열 - 모임 카드에 사용할 색상들
  const cardColors = [
    'border-l-blue-500',
    'border-l-green-500',
    'border-l-indigo-500',
    'border-l-purple-500',
    'border-l-pink-500',
    'border-l-yellow-500',
  ];

  return (
    <>
      <Header title="모임" />
      <main className="flex-1 overflow-auto p-4 space-y-5 pb-16 bg-slate-50">
        {/* 페이지 타이틀 */}
        <h2 className="text-xl font-semibold text-slate-800 flex items-center">
          <UsersRound className="h-5 w-5 mr-2 text-blue-600" />
          내 모임 목록
        </h2>
        
        {/* 검색 및 참가하기 영역 */}
        <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
          <CardContent className="p-5 space-y-4">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-slate-400" />
              <Input
                className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                placeholder="모임 이름 또는 설명으로 검색"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </CardContent>
        </Card>

        {/* 모임 목록 헤더 */}
        <div className="flex justify-between items-center mt-3">
          <div className="flex items-center">
            <Users className="h-5 w-5 mr-2 text-blue-600" />
            <h2 className="text-lg font-semibold text-slate-800">
              {filteredGroups.length > 0 ? `${filteredGroups.length}개의 모임` : "모임 목록"}
            </h2>
          </div>
          <Button
            className="rounded-xl bg-blue-600 hover:bg-blue-700 text-white transition-colors shadow-sm"
            onClick={() => router.push("/group/create-group")}
          >
            <Plus className="h-4 w-4 mr-2" />
            새 모임 만들기
          </Button>
        </div>

        {/* 모임 목록 */}
        <div className="space-y-3">
          {isLoading ? (
            <div className="flex flex-col items-center justify-center py-10">
              <RefreshCw className="h-10 w-10 text-blue-500 animate-spin mb-4" />
              <p className="text-slate-600">모임 목록을 불러오는 중...</p>
            </div>
          ) : filteredGroups.length > 0 ? (
            <>
              {filteredGroups.map((group, index) => (
                <Card
                  key={group.gatheringId}
                  className={`hover:shadow-md transition-all duration-200 transform hover:-translate-y-0.5 cursor-pointer border-0 shadow-sm rounded-xl border-l-4 ${cardColors[index % cardColors.length]}`}
                  onClick={() => router.push(`/group/${group.gatheringId}`)}
                >
                  <CardContent className="p-5">
                    <div className="flex justify-between items-start">
                      <div className="space-y-1.5">
                        <div className="font-semibold text-lg text-slate-800">
                          {group.gatheringName}
                        </div>
                        <div className="text-sm text-slate-600">
                          {group.gatheringIntroduction}
                        </div>
                        <div className="flex flex-wrap gap-2 mt-2">
                          <Badge variant="secondary" className="bg-slate-100 text-slate-800 rounded-full py-1 flex items-center">
                            <UsersRound className="h-3 w-3 mr-1" />
                            {group.memberCount}명
                          </Badge>
                          <Badge variant="secondary" className="bg-blue-100 text-blue-800 rounded-full py-1 flex items-center">
                            <Coins className="h-3 w-3 mr-1" />
                            {group.gatheringDeposit.toLocaleString()}원
                          </Badge>
                          <Badge variant="secondary" className="bg-green-100 text-green-800 rounded-full py-1 flex items-center">
                            <Calendar className="h-3 w-3 mr-1" />
                            {new Date(group.depositDate).getDate()}일
                          </Badge>
                        </div>
                      </div>
                      <div className="bg-slate-100 p-2 rounded-full">
                        <ChevronRight className="h-5 w-5 text-slate-500" />
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </>
          ) : (
            <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
              <CardContent className="p-10 text-center">
                <div className="flex flex-col items-center">
                  <div className="bg-slate-100 p-3 rounded-full mb-4">
                    <AlertCircle className="h-10 w-10 text-slate-400" />
                  </div>
                  <h3 className="text-lg font-medium text-slate-800 mb-2">
                    참여 중인 모임이 없습니다
                  </h3>
                  <p className="text-slate-500 mb-6 max-w-xs">
                    새 모임을 만들거나 초대 코드를 통해 모임에 참가해보세요.
                  </p>
                  <div className="flex gap-3">
                    <Button
                      variant="outline"
                      className="rounded-xl border-slate-200"
                      onClick={() => setIsJoinDialogOpen(true)}
                    >
                      <Link className="h-4 w-4 mr-2" />
                      초대 코드로 참가
                    </Button>
                    <Button
                      className="rounded-xl bg-blue-600 hover:bg-blue-700"
                      onClick={() => router.push("/group/create-group")}
                    >
                      <Plus className="h-4 w-4 mr-2" />
                      새 모임 만들기
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      </main>

      {/* 초대 코드 입력 다이얼로그 */}
      <Dialog open={isJoinDialogOpen} onOpenChange={setIsJoinDialogOpen}>
        <DialogContent className="border-0 shadow-md rounded-xl">
          <DialogHeader>
            <DialogTitle className="text-center text-xl flex items-center justify-center">
              <UserPlus className="h-5 w-5 mr-2 text-blue-600" />
              초대 코드로 모임 참가하기
            </DialogTitle>
            <DialogDescription className="text-center">
              모임 초대 링크나 코드를 입력하여 모임에 참가하세요.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-3">
            <div className="bg-blue-50 p-4 rounded-xl text-sm text-blue-700">
              <p className="flex items-start">
                <AlertCircle className="h-4 w-4 mr-2 mt-0.5 flex-shrink-0" />
                모임 초대 코드를 받은 경우 여기에 입력하세요. 총무의 승인 후 참여가 가능합니다.
              </p>
            </div>
            
            <div className="space-y-2">
              <div className="relative">
                <Link className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-slate-400" />
                <Input
                  placeholder="초대 코드 입력"
                  value={inviteCode}
                  onChange={(e) => setInviteCode(e.target.value)}
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
              </div>
              <p className="text-xs text-slate-500 pl-2">
                * 전체 URL을 붙여넣거나 코드만 입력해도 됩니다.
              </p>
            </div>
          </div>
          <DialogFooter>
            <Button
              className="w-full py-6 rounded-xl bg-blue-600 hover:bg-blue-700"
              onClick={handleJoinWithInviteCode}
              disabled={isJoining}
            >
              {isJoining ? (
                <div className="flex items-center justify-center">
                  <RefreshCw className="h-4 w-4 mr-2 animate-spin" />
                  처리 중...
                </div>
              ) : (
                <div className="flex items-center justify-center">
                  <UserPlus className="h-5 w-5 mr-2" />
                  참가 신청하기
                </div>
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}
