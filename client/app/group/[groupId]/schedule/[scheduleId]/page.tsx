"use client";
import React, { use, useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";
import {
  Calendar,
  Clock,
  MapPin,
  Users,
  DollarSign,
  MessageSquare,
  SendHorizontal,
  Wallet,
  AlertCircle,
  RefreshCw,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Header } from "@/components/Header";
import axios, { AxiosInstance } from "axios";
import { toast } from "@/components/ui/use-toast";
import { publicApi, authApi } from "@/lib/api";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

interface Attendee {
  userId: number;
  username: string;
}

interface ScheduleManager {
  userId: number;
  username: string;
  accountNumber?: string;
}

interface ScheduleData {
  gatheringId: number; // Long
  gatheringName: string; // String
  scheduleId: number; // Long
  scheduleTitle: string; // String
  subManagerName: string; // String
  scheduleStartTime: string; // LocalDateTime
  schedulePlace: string; // String
  perBudget: number; // Long
  scheduleDetail: string; // String
  attendeeCount: number; // int
  subManager : boolean; // boolean
  scheduleAccountBalance : number // Long
}

export default function ScheduleDetailPage() {
  const router = useRouter();
  const params = useParams();
  const { groupId, scheduleId } = params;
  const [scheduleData, setScheduleData] = useState<ScheduleData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isEndDialogOpen, setIsEndDialogOpen] = useState(false);

  useEffect(() => {
    const fetchScheduleData = async () => {
      try {
        const data = await authApi.get<ScheduleData>(
          `/api/v1/schedule/${scheduleId}/detail`
        );
        console.log("API 응답:", data);
        setScheduleData(data as unknown as ScheduleData);
      } catch (error) {
        console.error("일정 데이터를 불러오는데 실패했습니다:", error);
        toast({
          title: "데이터 로드 실패",
          description: "일정 정보를 불러오는데 실패했습니다.",
          variant: "destructive",
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchScheduleData();
  }, [scheduleId]);

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString("ko-KR", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  };

  const formatTime = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString("ko-KR", {
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const handleEndSchedule = async () => {
    try {
      await authApi.delete(`api/v1/schedule/${scheduleId}`);

      toast({
        title: "일정 종료 완료",
        description: "일정이 성공적으로 종료되었습니다.",
      });

      router.push(`/group/${groupId}`); // 그룹 메인 페이지로 이동
    } catch (error) {
      toast({
        title: "오류 발생",
        description: "일정 종료 처리 중 오류가 발생했습니다. 다시 시도해주세요.",
        variant: "destructive",
      });
    }
    setIsEndDialogOpen(false);
  };

  if (isLoading) {
    return (
      <>
        <Header title="일정 상세" showBackButton />
        <main className="flex-1 overflow-auto p-4 bg-slate-50">
          <div className="flex items-center justify-center h-[80vh]">
            <div className="flex flex-col items-center gap-2">
              <RefreshCw className="h-8 w-8 text-blue-600 animate-spin" />
              <span className="text-sm text-slate-600">로딩 중...</span>
            </div>
          </div>
        </main>
      </>
    );
  }

  if (!scheduleData) {
    return (
      <>
        <Header title="일정 상세" showBackButton />
        <main className="flex-1 overflow-auto p-4 bg-slate-50">
          <div className="flex items-center justify-center h-[80vh]">
            <div className="text-center space-y-2">
              <AlertCircle className="h-8 w-8 text-red-500 mx-auto" />
              <p className="text-slate-600">일정 정보를 불러올 수 없습니다.</p>
            </div>
          </div>
        </main>
      </>
    );
  }

  return (
    <>
      <Header title="일정 상세" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16 bg-slate-50">
        <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
          <CardContent className="p-6 space-y-6">
            {/* 일정 헤더 */}
            <div className="space-y-2">
              <Badge variant="outline" className="bg-blue-50 text-blue-600 border-blue-200 mb-2">
                {scheduleData.gatheringName}
              </Badge>
              <h2 className="text-2xl font-bold text-slate-800">
                {scheduleData.scheduleTitle}
              </h2>
              <div className="flex items-center gap-2">
                <Avatar className="h-6 w-6">
                  <AvatarFallback className="bg-blue-100 text-blue-600 text-xs">
                    {scheduleData.subManagerName.slice(0, 2)}
                  </AvatarFallback>
                </Avatar>
                <span className="text-slate-600 text-sm">
                  {scheduleData.subManagerName} 주최
                </span>
              </div>
            </div>

            {/* 일정 정보 카드 */}
            <div className="grid grid-cols-2 gap-4">
              <Card className="border-0 shadow-sm rounded-xl bg-gradient-to-br from-blue-50 to-white">
                <CardContent className="p-4">
                  <div className="flex items-center text-slate-700 mb-1">
                    <Calendar className="h-4 w-4 mr-2 text-blue-500" />
                    <span className="text-sm font-medium">날짜</span>
                  </div>
                  <span className="text-slate-600">
                    {formatDate(scheduleData.scheduleStartTime)}
                  </span>
                </CardContent>
              </Card>

              <Card className="border-0 shadow-sm rounded-xl bg-gradient-to-br from-blue-50 to-white">
                <CardContent className="p-4">
                  <div className="flex items-center text-slate-700 mb-1">
                    <Clock className="h-4 w-4 mr-2 text-blue-500" />
                    <span className="text-sm font-medium">시간</span>
                  </div>
                  <span className="text-slate-600">
                    {formatTime(scheduleData.scheduleStartTime)}
                  </span>
                </CardContent>
              </Card>

              <Card className="border-0 shadow-sm rounded-xl bg-gradient-to-br from-blue-50 to-white">
                <CardContent className="p-4">
                  <div className="flex items-center text-slate-700 mb-1">
                    <MapPin className="h-4 w-4 mr-2 text-blue-500" />
                    <span className="text-sm font-medium">장소</span>
                  </div>
                  <span className="text-slate-600">
                    {scheduleData.schedulePlace}
                  </span>
                </CardContent>
              </Card>

              <Card className="border-0 shadow-sm rounded-xl bg-gradient-to-br from-blue-50 to-white">
                <CardContent className="p-4">
                  <div className="flex items-center text-slate-700 mb-1">
                    <Users className="h-4 w-4 mr-2 text-blue-500" />
                    <span className="text-sm font-medium">참석 인원</span>
                  </div>
                  <span className="text-slate-600">
                    {scheduleData.attendeeCount}명 참석 예정
                  </span>
                </CardContent>
              </Card>
            </div>

            {/* 예산 정보 */}
            <Card className="border-0 shadow-sm rounded-xl bg-gradient-to-br from-green-50 to-white">
              <CardContent className="p-4 space-y-3">
                <div className="flex items-center justify-between">
                  <div className="flex items-center text-slate-700">
                    <DollarSign className="h-4 w-4 mr-2 text-green-500" />
                    <span className="text-sm font-medium">1인당 예산</span>
                  </div>
                  <span className="text-green-600 font-medium">
                    {scheduleData.perBudget.toLocaleString()}원
                  </span>
                </div>
                <div className="flex items-center justify-between pt-2 border-t border-green-100">
                  <div className="flex items-center text-slate-700">
                    <Wallet className="h-4 w-4 mr-2 text-green-500" />
                    <span className="text-sm font-medium">총 모인 금액</span>
                  </div>
                  <span className="text-green-600 font-medium">
                    {scheduleData.scheduleAccountBalance.toLocaleString()}원
                  </span>
                </div>
              </CardContent>
            </Card>

            {/* 일정 설명 */}
            <div className="bg-slate-50 rounded-xl p-4 space-y-2">
              <div className="flex items-center text-slate-700 mb-2">
                <MessageSquare className="h-4 w-4 mr-2 text-blue-500" />
                <h3 className="font-medium">일정 설명</h3>
              </div>
              <p className="text-slate-600 text-sm leading-relaxed">
                {scheduleData.scheduleDetail}
              </p>
            </div>

            <div className="space-y-2">
              <div className="flex items-center text-gray-600">
                <Calendar className="h-5 w-5 mr-2" />
                <span>{formatDate(scheduleData.scheduleStartTime)}</span>
              </div>
              <div className="flex items-center text-gray-600">
                <Clock className="h-5 w-5 mr-2" />
                <span>{formatTime(scheduleData.scheduleStartTime)}</span>
              </div>
              <div className="flex items-center text-gray-600">
                <MapPin className="h-5 w-5 mr-2" />
                <span>{scheduleData.schedulePlace}</span>
              </div>
              <div className="flex items-center text-gray-600">
                <Users className="h-5 w-5 mr-2" />
                <span>{scheduleData.attendeeCount}명 참석 예정</span>
              </div>
              <div className="flex items-center text-gray-600">
                <DollarSign className="h-5 w-5 mr-2" />
                <span>
                  1인당 예산 {scheduleData.perBudget.toLocaleString()}원
                </span>
              </div>
              <div className="flex items-center text-gray-600">
                <Wallet className="h-5 w-5 mr-2" />
                <span>
                  총 금액 {scheduleData.scheduleAccountBalance.toLocaleString()}원
                </span>
              </div>
            </div>

            <div>
              <h3 className="font-semibold mb-2">설명</h3>
              <p className="text-gray-600">{scheduleData.scheduleDetail}</p>
            </div>

            {/* 송금하기 버튼과 일정 종료 버튼 컨테이너 */}
            <div className="space-y-3">
              {scheduleData.subManager && (
                <>
                  <Button
                    onClick={() =>
                      router.push(`/group/${groupId}/schedule/${scheduleId}/send`)
                    }
                    className="w-full py-6 rounded-xl bg-blue-600 hover:bg-blue-700"
                  >
                    <SendHorizontal className="h-4 w-4 mr-2" />
                    송금하기
                  </Button>

                  <Button
                    variant="outline"
                    className="w-full py-6 rounded-xl border-red-200 text-red-600 hover:bg-red-50"
                    onClick={() => setIsEndDialogOpen(true)}
                  >
                    <AlertCircle className="h-4 w-4 mr-2" />
                    일정 종료하기
                  </Button>
                </>
              )}
            </div>
          </CardContent>
        </Card>
      </main>

      {/* 일정 종료 확인 다이얼로그 */}
      <Dialog open={isEndDialogOpen} onOpenChange={setIsEndDialogOpen}>
        <DialogContent className="sm:max-w-md rounded-xl">
          <DialogHeader>
            <DialogTitle className="flex items-center text-red-600">
              <AlertCircle className="h-5 w-5 mr-2" />
              일정 종료
            </DialogTitle>
            <DialogDescription className="text-slate-600">
              정말로 <span className="font-medium text-slate-700">{scheduleData.scheduleTitle}</span> 일정을 종료하시겠습니까?
              <div className="mt-2 p-3 bg-red-50 rounded-lg border border-red-100">
                <p className="text-red-600 text-sm">
                  일정 종료 시 더 이상 참석 관리 및 송금이 불가능합니다.
                  이 작업은 되돌릴 수 없습니다.
                </p>
              </div>
            </DialogDescription>
          </DialogHeader>
          <DialogFooter className="gap-2 sm:gap-0">
            <Button
              variant="outline"
              onClick={() => setIsEndDialogOpen(false)}
              className="rounded-xl border-slate-200"
            >
              취소
            </Button>
            <Button 
              variant="destructive" 
              onClick={handleEndSchedule}
              className="rounded-xl bg-red-600 hover:bg-red-700"
            >
              종료하기
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}
