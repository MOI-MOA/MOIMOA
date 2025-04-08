"use client"

import { useRouter } from "next/navigation"
import { Calendar, Clock, MapPin, Users, ChevronRight, Bell } from "lucide-react"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Header } from "@/components/Header"
import { useState, useEffect } from "react"
import { format } from "date-fns"
import { toast } from "@/components/ui/use-toast"
import { authApi } from "@/lib/api"
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
      await authApi.post(`api/v1/schedule/${scheduleId}/attend-reject`)
      
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
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16 bg-slate-50">
        <div className="space-y-3">
          <h2 className="text-xl font-semibold text-slate-800 flex items-center">
            <Bell className="h-5 w-5 mr-2 text-blue-600" />
            확인이 필요한 일정 ({schedules.length}개)
          </h2>
          
          {schedules.length > 0 ? (
            <div className="space-y-3">
              {schedules.map((schedule, index) => (
                <Card
                  key={schedule.scheduleId}
                  className="border-0 shadow-sm hover:shadow-md rounded-xl overflow-hidden transition-all duration-200 cursor-pointer hover:translate-y-[-2px]"
                  onClick={() => router.push(`/group/${schedule.gatheringId}/schedule/${schedule.scheduleId}`)}
                >
                  <CardContent className="p-0">
                    <div className={`border-l-4 ${index % 3 === 0 ? 'border-indigo-500' : index % 3 === 1 ? 'border-teal-500' : 'border-amber-500'} p-4`}>
                      <div className="flex justify-between items-start">
                        <div className="space-y-2.5">
                          <div className="font-medium text-lg text-slate-800">
                            {schedule.scheduleTitle}
                          </div>
                          <div className="text-sm font-medium text-blue-600">
                            {schedule.gatheringName}
                          </div>
                          <div className="flex flex-wrap items-center text-sm text-slate-600 gap-3">
                            <div className="flex items-center px-2.5 py-1 bg-slate-100 rounded-full">
                              <Calendar className="h-3.5 w-3.5 mr-1.5 text-slate-500" />
                              {format(new Date(schedule.scheduleStartTime), "yyyy.MM.dd")}
                            </div>
                            <div className="flex items-center px-2.5 py-1 bg-slate-100 rounded-full">
                              <Clock className="h-3.5 w-3.5 mr-1.5 text-slate-500" />
                              {format(new Date(schedule.scheduleStartTime), "HH:mm")}
                            </div>
                            <div className="flex items-center px-2.5 py-1 bg-slate-100 rounded-full">
                              <MapPin className="h-3.5 w-3.5 mr-1.5 text-slate-500" />
                              {schedule.schedulePlace}
                            </div>
                            <div className="flex items-center px-2.5 py-1 bg-slate-100 rounded-full">
                              <Users className="h-3.5 w-3.5 mr-1.5 text-slate-500" />
                              {schedule.attendeeCount}명
                            </div>
                          </div>
                          
                          <div className="flex space-x-3 mt-1">
                            <Button 
                              size="sm"
                              className="bg-blue-600 hover:bg-blue-700 text-white rounded-full px-4"
                              onClick={(e) => handleAttend(schedule.scheduleId, e)}
                            >
                              참석
                            </Button>
                            <Button 
                              variant="outline"
                              size="sm"
                              className="border-slate-300 hover:bg-slate-100 text-slate-700 rounded-full px-4"
                              onClick={(e) => handleCancel(schedule.scheduleId, e)}
                            >
                              거절
                            </Button>
                          </div>
                        </div>
                        <ChevronRight className="h-5 w-5 text-slate-400" />
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : (
            <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
              <CardContent className="p-8 text-center text-slate-500 bg-white">
                <div className="flex flex-col items-center">
                  <div className="bg-slate-100 p-4 rounded-full mb-3">
                    <Bell className="h-8 w-8 text-slate-400" />
                  </div>
                  <p className="font-medium">확인이 필요한 일정이 없습니다</p>
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      </main>
    </>
  )
}

