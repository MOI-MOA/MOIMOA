"use client";

import { useState, useMemo, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Bell, MapPin, Clock, ChevronRight, Calendar as CalendarIcon, Users } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Calendar } from "@/components/ui/calendar";
import { Header } from "@/components/Header";
import { format } from "date-fns";
import { toast } from "@/components/ui/use-toast";
import { publicApi, authApi } from "@/lib/api";
type DateData = {
  date: number;
};

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

type HomeData = {
  uncheckScheduleCount: number;
  dateList: DateData[];
  todayScheduleList: ScheduleData[];
  upcomingScheduleList: ScheduleData[];
};

export default function HomePage() {
  const router = useRouter();
  const [selectedDate, setSelectedDate] = useState<Date | undefined>(
    new Date()
  );
  const [currentMonth, setCurrentMonth] = useState<Date>(new Date());
  const [uncheckScheduleCount, setUncheckScheduleCount] = useState(0);
  const [dateList, setDateList] = useState<DateData[]>([]);
  const [todaySchedules, setTodaySchedules] = useState<ScheduleData[]>([]);
  const [upcomingSchedules, setUpcomingSchedules] = useState<ScheduleData[]>(
    []
  );
  const [isLoading, setIsLoading] = useState(true);

  // API에서 데이터 가져오기
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = (await authApi.get<HomeData>(
          "api/v1/main"
        )) as unknown as HomeData;
        console.log(response);

        setUncheckScheduleCount(response.uncheckScheduleCount);
        setDateList(response.dateList);
        setTodaySchedules(response.todayScheduleList);
        setUpcomingSchedules(response.upcomingScheduleList);
      } catch (error) {
        console.error("일정을 가져오는데 실패했습니다:", error);
        toast({
          title: "데이터 로딩 실패",
          description: "일정 정보를 가져오는데 실패했습니다.",
          variant: "destructive",
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, []);

  // 선택된 날짜의 일정 가져오기
  const fetchSelectedDateSchedules = async (date: Date) => {
    try {
      const year = date.getFullYear();
      const month = date.getMonth() + 1;
      const day = date.getDate();

      const response = (await authApi.get<ScheduleData[]>(
        `api/v1/main/schedule/${year}/${month}/${day}`
      )) as unknown as ScheduleData[];
      console.log(response);
      setTodaySchedules(response);
    } catch (error) {
      console.error("선택된 날짜의 일정을 가져오는데 실패했습니다:", error);
      toast({
        title: "데이터 로딩 실패",
        description: "선택된 날짜의 일정 정보를 가져오는데 실패했습니다.",
        variant: "destructive",
      });
    }
  };

  // 월별 일정 날짜 목록 가져오기
  const fetchMonthlyScheduleDates = async (date: Date) => {
    try {
      const year = date.getFullYear();
      const month = date.getMonth() + 1;

      const response = (await authApi.get<DateData[]>(
        `api/v1/main/schedule/${year}/${month}`
      )) as unknown as DateData[];
      console.log(response);
      setDateList(response);
    } catch (error) {
      console.error("월별 일정 날짜를 가져오는데 실패했습니다:", error);
      toast({
        title: "데이터 로딩 실패",
        description: "월별 일정 날짜 정보를 가져오는데 실패했습니다.",
        variant: "destructive",
      });
    }
  };

  // 달력 데이터 포맷 변환
  const scheduleData = useMemo(() => {
    const formattedData: Record<string, any[]> = {};

    dateList.forEach((dateData) => {
      const dateStr = format(
        new Date(
          currentMonth.getFullYear(),
          currentMonth.getMonth(),
          dateData.date
        ),
        "yyyy-MM-dd"
      );
      formattedData[dateStr] = todaySchedules
        .filter((schedule) => {
          const scheduleDate = format(
            new Date(schedule.scheduleStartTime),
            "yyyy-MM-dd"
          );
          return scheduleDate === dateStr;
        })
        .map((schedule) => ({
          id: schedule.scheduleId,
          groupId: schedule.gatheringId,
          title: schedule.scheduleTitle,
          time: format(new Date(schedule.scheduleStartTime), "HH:mm"),
          location: schedule.schedulePlace,
          group: schedule.gatheringName,
          participants: schedule.attendeeCount,
        }));
    });

    return formattedData;
  }, [dateList, todaySchedules, currentMonth]);

  // 선택된 날짜의 일정
  const selectedDateSchedules = useMemo(() => {
    if (!selectedDate) return [];
    const dateStr = format(selectedDate, "yyyy-MM-dd");
    return scheduleData[dateStr] || [];
  }, [selectedDate, scheduleData]);

  // 날짜 선택 핸들러
  const handleDateSelect = async (date: Date | undefined) => {
    setSelectedDate(date);
    if (date) {
      await fetchSelectedDateSchedules(date);
    }
  };

  // 월 변경 핸들러
  const handleMonthChange = async (date: Date) => {
    setCurrentMonth(date);
    await fetchMonthlyScheduleDates(date);
  };

  return (
    <>
      <Header />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16 bg-slate-50">
        {/* 미확인 일정 알림 */}
        {uncheckScheduleCount > 0 && (
          <div className="animate-fadeIn">
            <Button
              variant="outline"
              className="w-full border border-blue-100 bg-white hover:bg-blue-50 text-blue-700 shadow-sm rounded-lg transition-all duration-200"
              onClick={() => router.push("/uncheck-schedule")}
            >
              <div className="flex items-center justify-between w-full">
                <div className="flex items-center">
                  <div className="bg-blue-100 p-1.5 rounded-full mr-3">
                    <Bell className="h-4 w-4 text-blue-700" />
                  </div>
                  <span className="font-medium">미확인 일정</span>
                </div>
                <div className="flex items-center">
                  <Badge className="bg-blue-600 hover:bg-blue-700 text-white font-semibold mr-2 px-2.5">
                    {uncheckScheduleCount}
                  </Badge>
                  <ChevronRight className="h-4 w-4" />
                </div>
              </div>
            </Button>
          </div>
        )}

        {/* 이번 달 일정 - 달력 */}
        <div className="space-y-3">
          <h2 className="text-xl font-semibold text-slate-800 flex items-center">
            <CalendarIcon className="h-5 w-5 mr-2 text-blue-600" />
            이번 달 일정
          </h2>
          <Card className="border-0 shadow-sm overflow-hidden rounded-xl">
            <CardContent className="p-4">
              <Calendar
                selected={selectedDate}
                onSelect={handleDateSelect}
                className="rounded-lg border-0"
                schedules={scheduleData}
                month={currentMonth}
                onMonthChange={handleMonthChange}
                fromMonth={new Date(2023, 0)}
                toMonth={new Date(2030, 11)}
              />
            </CardContent>
          </Card>
        </div>

        {/* 선택된 날짜의 일정 목록 */}
        {selectedDate && (
          <div className="space-y-3">
            <h2 className="text-xl font-semibold text-slate-800 flex items-center">
              <span className="inline-flex items-center justify-center w-8 h-8 bg-blue-100 text-blue-700 rounded-full mr-2 text-sm font-bold">
                {format(selectedDate, "d")}
              </span>
              {format(selectedDate, "yyyy년 MM월 dd일")} 일정
            </h2>
            
            {selectedDateSchedules.length > 0 ? (
              <div className="space-y-3">
                {selectedDateSchedules.map((schedule) => (
                  <Card
                    key={schedule.id}
                    className="border-0 shadow-sm hover:shadow-md rounded-xl overflow-hidden transition-all duration-200 cursor-pointer hover:translate-y-[-2px]"
                    onClick={() => router.push(`/group/${schedule.groupId}/schedule/${schedule.id}`)}
                  >
                    <CardContent className="p-0">
                      <div className="border-l-4 border-blue-500 p-4">
                        <div className="flex justify-between items-start">
                          <div className="space-y-2.5">
                            <div className="font-medium text-lg text-slate-800">
                              {schedule.title}
                            </div>
                            <div className="text-sm font-medium text-blue-600">
                              {schedule.group}
                            </div>
                            <div className="flex flex-wrap items-center text-sm text-slate-600 gap-3">
                              <div className="flex items-center px-2.5 py-1 bg-slate-100 rounded-full">
                                <Clock className="h-3.5 w-3.5 mr-1.5 text-slate-500" />
                                {schedule.time}
                              </div>
                              <div className="flex items-center px-2.5 py-1 bg-slate-100 rounded-full">
                                <MapPin className="h-3.5 w-3.5 mr-1.5 text-slate-500" />
                                {schedule.location}
                              </div>
                              <div className="flex items-center px-2.5 py-1 bg-slate-100 rounded-full">
                                <Users className="h-3.5 w-3.5 mr-1.5 text-slate-500" />
                                {schedule.participants}명
                              </div>
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
                      <CalendarIcon className="h-8 w-8 text-slate-400" />
                    </div>
                    <p className="font-medium">해당 날짜에 일정이 없습니다</p>
                  </div>
                </CardContent>
              </Card>
            )}
          </div>
        )}

        {/* 다가오는 일정 */}
        <div className="space-y-3">
          <h2 className="text-xl font-semibold text-slate-800 flex items-center">
            <Clock className="h-5 w-5 mr-2 text-blue-600" />
            다가오는 일정
          </h2>
          
          {upcomingSchedules.length > 0 ? (
            <div className="space-y-3">
              {upcomingSchedules.map((schedule, index) => (
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
                              <Clock className="h-3.5 w-3.5 mr-1.5 text-slate-500" />
                              {format(new Date(schedule.scheduleStartTime), "yyyy.MM.dd HH:mm")}
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
                    <Clock className="h-8 w-8 text-slate-400" />
                  </div>
                  <p className="font-medium">다가오는 일정이 없습니다</p>
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      </main>
    </>
  );
}
