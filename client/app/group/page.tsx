"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Plus, Search, ChevronRight, Link } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Header } from "@/components/Header";
import { toast } from "@/components/ui/use-toast";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";

interface Group {
  id: number;
  name: string;
  description: string;
  members: number;
  totalAmount: number;
  currency: string;
}

export default function GroupsPage() {
  const [searchTerm, setSearchTerm] = useState("");
  const [inviteCode, setInviteCode] = useState("");
  const [isJoinDialogOpen, setIsJoinDialogOpen] = useState(false);
  const [isJoining, setIsJoining] = useState(false);
  const [groups, setGroups] = useState<Group[]>(() => {
    if (typeof window !== "undefined") {
      const savedGroups = localStorage.getItem("groups");
      if (savedGroups) {
        return JSON.parse(savedGroups);
      }
    }
    return [
      {
        id: 1,
        name: "SSAFY 12기",
        description: "SSAFY 12기 모임",
        members: 5,
        totalAmount: 150000,
        currency: "KRW",
      },
    ];
  });
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();

  // 그룹 데이터가 변경될 때마다 로컬 스토리지에 저장
  useEffect(() => {
    localStorage.setItem("groups", JSON.stringify(groups));
  }, [groups]);

  const filteredGroups = groups.filter(
    (group) =>
      group.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      group.description.toLowerCase().includes(searchTerm.toLowerCase())
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
      // 실제로는 API 호출로 초대 코드 검증 및 모임 참가 처리
      await new Promise((resolve) => setTimeout(resolve, 1500)); // 임시 지연

      // 새로운 그룹 추가
      const newGroup: Group = {
        id: groups.length + 1,
        name: "새로운 모임",
        description: "초대 코드로 참가한 모임",
        members: 1,
        totalAmount: 0,
        currency: "KRW",
      };

      setGroups((prevGroups) => [...prevGroups, newGroup]);

      // 성공 시 처리
      toast({
        title: "모임 참가 신청 완료",
        description:
          "모임 참가 신청이 완료되었습니다. 총무의 승인을 기다려주세요.",
        duration: 3000,
      });
      setIsJoinDialogOpen(false);
      setInviteCode("");
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

  return (
    <>
      <Header title="모임" />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16">
        {/* Search Bar */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
          <Input
            className="pl-9 py-2 bg-white border-gray-200"
            placeholder="모임 이름, 모임 설명, 참여인원수"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        {/* 초대 링크로 참가하기 버튼 */}
        <Button
          variant="outline"
          className="w-full border-blue-200 bg-blue-50 hover:bg-blue-100 text-blue-700"
          onClick={() => setIsJoinDialogOpen(true)}
        >
          <Link className="h-4 w-4 mr-2" />
          초대 링크로 모임 참가하기
        </Button>

        {/* Group List Header */}
        <div className="flex justify-between items-center">
          <h2 className="text-lg font-semibold">모임</h2>
          <Button
            className="bg-blue-500 hover:bg-blue-600 text-white"
            onClick={() => router.push("/group/create-group")}
          >
            <Plus className="h-4 w-4 mr-2" />새 모임 만들기
          </Button>
        </div>

        {/* Group List */}
        <div className="space-y-3">
          {filteredGroups.map((group) => (
            <Card
              key={group.id}
              className="hover:shadow-md transition-shadow cursor-pointer"
              onClick={() => router.push(`/group/${group.id}`)}
            >
              <CardContent className="p-4">
                <div className="flex justify-between items-start">
                  <div>
                    <div className="font-medium text-lg text-gray-800">
                      {group.name}
                    </div>
                    <div className="text-sm text-gray-500">
                      {group.description}
                    </div>
                    <div className="text-sm mt-1 text-gray-600">
                      참여인원: {group.members}명
                      <Badge
                        variant="secondary"
                        className="ml-2 bg-blue-100 text-blue-800"
                      >
                        {group.totalAmount.toLocaleString()} {group.currency}
                      </Badge>
                    </div>
                  </div>
                  <ChevronRight className="h-5 w-5 text-gray-400" />
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </main>

      {/* 초대 코드 입력 다이얼로그 */}
      <Dialog open={isJoinDialogOpen} onOpenChange={setIsJoinDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>초대 코드로 모임 참가하기</DialogTitle>
            <DialogDescription>
              모임 초대 링크나 코드를 입력하여 모임에 참가하세요.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Input
                placeholder="초대 코드 입력"
                value={inviteCode}
                onChange={(e) => setInviteCode(e.target.value)}
              />
              <p className="text-xs text-gray-500">
                * 전체 URL을 붙여넣거나 코드만 입력해도 됩니다.
              </p>
            </div>
          </div>
          <DialogFooter>
            <Button
              className="w-full"
              onClick={handleJoinWithInviteCode}
              disabled={isJoining}
            >
              {isJoining ? "처리 중..." : "참가 신청하기"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}
