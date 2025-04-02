"use client"

import { useRouter } from "next/navigation"
import { Calendar, Clock, MapPin, Users, ChevronRight } from "lucide-react"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Header } from "@/components/Header"
import { useState, useEffect } from "react"
import axios from "axios"
import { format } from "date-fns"
import { toast } from "@/components/ui/use-toast"
import { publicApi, authApi } from "@/lib/api"
import { Button } from "@/components/ui/button"

type ScheduleData = {
  gatheringId: number;
  gatheringName: string;
  scheduleId: number;
  scheduleTitle: string;
  schedulePlace: string;
  scheduleStartTime: string;
  perBudget: number;
  attendeeCount: number;
};


export default function UncheckSchedulePage() {
  const router = useRouter()
  const [schedules, setSchedules] = useState<ScheduleData[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchSchedules = async () => {
      try {
        const response = await authApi.get<ScheduleData[]>(
          "api/v1/main/schedule/uncheck"
        ) as unknown as ScheduleData[];
        console.log(response);
        setSchedules(response);
      } catch (error) {
        console.error("미확인 일정을 가져오는데 실패했습니다:", error);
        toast({
          title: "데이터 로딩 실패",
          description: "미확인 일정 정보를 가져오는데 실패했습니다.",
          variant: "destructive",
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchSchedules();
  }, []);

  // 참석 처리 함수
  const handleAttend = async (scheduleId: number, e: React.MouseEvent) => {
    e.stopPropagation() // 카드 클릭 이벤트 전파 방지
    try {
      await authApi.post(`api/v1/schedule/${scheduleId}/attend`)
      
      // 성공 시 해당 일정을 목록에서 제거
      setSchedules(prev => prev.filter(schedule => schedule.scheduleId !== scheduleId))
      
      toast({
        title: "참석 완료",
        description: "일정 참석이 완료되었습니다.",
      })
    } catch (error) {
      toast({
        title: "참석 실패",
        description: "알 수 없는 오류가 발생했습니다.",
        variant: "destructive",
      })
    }
  }

  // 거절 처리 함수
  const handleCancel = async (scheduleId: number, e: React.MouseEvent) => {
    e.stopPropagation() // 카드 클릭 이벤트 전파 방지
    try {
      await authApi.post(`api/v1/schedule/${scheduleId}/cancel`)
      
      // 성공 시 해당 일정을 목록에서 제거
      setSchedules(prev => prev.filter(schedule => schedule.scheduleId !== scheduleId))
      
      toast({
        title: "거절 완료",
        description: "일정 거절이 완료되었습니다.",
      })
    } catch (error) {
      toast({
        title: "거절 실패",
        description: "알 수 없는 오류가 발생했습니다.",
        variant: "destructive",
      })
    }
  }

  return (
    <>
      <Header title="미확인 일정" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16">
        {schedules.map((schedule) => (
          <Card
            key={schedule.scheduleId}
            className="hover:shadow-md transition-shadow cursor-pointer"
            onClick={() => router.push(`/group/${schedule.gatheringId}/schedule/${schedule.scheduleId}`)}
          >
            <CardContent className="p-4">
              <div className="flex justify-between items-start">
                <div className="space-y-2">
                  <div className="font-medium text-lg">{schedule.scheduleTitle}</div>
                  <div className="text-sm text-gray-500">{schedule.gatheringName}</div>
                  <div className="flex items-center text-sm text-gray-600 space-x-4">
                    <div className="flex items-center">
                      <Calendar className="h-4 w-4 mr-1" />
                      {format(new Date(schedule.scheduleStartTime), "yyyy.MM.dd")}
                    </div>
                    <div className="flex items-center">
                      <Clock className="h-4 w-4 mr-1" />
                      {format(new Date(schedule.scheduleStartTime), "HH:mm")}
                    </div>
                  </div>
                  <div className="flex items-center text-sm text-gray-600">
                    <MapPin className="h-4 w-4 mr-1" />
                    {schedule.schedulePlace}
                  </div>
                  <div className="flex space-x-2 mt-2">
                    <Button 
                      variant="default"
                      size="sm"
                      onClick={(e) => handleAttend(schedule.scheduleId, e)}
                    >
                      참석
                    </Button>
                    <Button 
                      variant="outline"
                      size="sm"
                      onClick={(e) => handleCancel(schedule.scheduleId, e)}
                    >
                      거절
                    </Button>
                  </div>
                </div>
                <div className="flex flex-col items-end">
                  <Badge variant="secondary" className="mb-2">
                    <Users className="h-3 w-3 mr-1" />
                    {schedule.attendeeCount}명
                  </Badge>
                  <ChevronRight className="h-5 w-5 text-gray-400" />
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </main>
    </>
  )
}

