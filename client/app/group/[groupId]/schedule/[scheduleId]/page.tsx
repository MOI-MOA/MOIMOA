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
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Header } from "@/components/Header";
import axios, { AxiosInstance } from "axios";
import { toast } from "@/components/ui/use-toast";
import { publicApi, authApi } from "@/lib/api";

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

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-screen">
        로딩 중...
      </div>
    );
  }

  if (!scheduleData) {
    return (
      <div className="flex items-center justify-center h-screen">
        일정 정보를 불러올 수 없습니다.
      </div>
    );
  }

  return (
    <>
      <Header title="일정 상세" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16">
        <Card>
          <CardContent className="p-6 space-y-4">
            <div>
              <h2 className="text-2xl font-bold">
                {scheduleData.scheduleTitle}
              </h2>
              <p className="text-gray-500">
                {scheduleData.subManagerName} 주최
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

            {scheduleData.subManager?
            (<Button
              onClick={() =>
                router.push(`/group/${groupId}/schedule/${scheduleId}/send`)
              }
              className="bg-gray-200 hover:bg-gray-400 text-black"
            >
              <SendHorizontal className="h-4 w-4 mr-2" />
              송금하기
            </Button>) :(
              <a></a>
            )
            }   

          </CardContent>
        </Card>
      </main>
    </>
  );
}
