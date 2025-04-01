"use client";

import { useState, useMemo, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Bell, MapPin, Clock, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Calendar } from "@/components/ui/calendar";
import { Header } from "@/components/Header";
import { format } from "date-fns";
import { toast } from "@/components/ui/use-toast";
import { publicApi } from "@/lib/api";
import { LOCALHOST } from "@/lib/constants";
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
        const response = (await publicApi.get<HomeData>(
          LOCALHOST + "api/v1/main"
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

      const response = (await publicApi.get<ScheduleData[]>(
        LOCALHOST + `api/v1/main/schedule/${year}/${month}/${day}`
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

      const response = (await publicApi.get<DateData[]>(
        LOCALHOST + `api/v1/main/schedule/${year}/${month}`
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
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16">
        {/* 미확인 일정 알림 */}
        {uncheckScheduleCount > 0 && (
          <Button
            variant="outline"
            className="w-full border-blue-200 bg-blue-50 hover:bg-blue-100 text-blue-700"
            onClick={() => router.push("/uncheck-schedule")}
          >
            <Bell className="h-4 w-4 mr-2" />
            미확인 일정 {uncheckScheduleCount}개
            <ChevronRight className="h-4 w-4 ml-auto" />
          </Button>
        )}

        {/* 이번 달 일정 - 달력 */}
        <div className="space-y-2">
          <h2 className="text-lg font-semibold">이번 달 일정</h2>
          <Card>
            <CardContent className="p-3">
              <Calendar
                selected={selectedDate}
                onSelect={handleDateSelect}
                className="rounded-md border"
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
          <div className="space-y-2">
            <h2 className="text-lg font-semibold">
              {format(selectedDate, "yyyy년 MM월 dd일")} 일정
            </h2>
            {selectedDateSchedules.length > 0 ? (
              <div className="space-y-3">
                {selectedDateSchedules.map((schedule) => (
                  <Card
                    key={schedule.id}
                    className="hover:shadow-md transition-shadow cursor-pointer"
                    onClick={() =>
                      router.push(
                        `/group/${schedule.groupId}/schedule/${schedule.id}`
                      )
                    }
                  >
                    <CardContent className="p-4">
                      <div className="flex justify-between items-start">
                        <div className="space-y-2">
                          <div className="font-medium text-lg">
                            {schedule.title}
                          </div>
                          <div className="text-sm text-gray-500">
                            {schedule.group}
                          </div>
                          <div className="flex items-center text-sm text-gray-600 space-x-4">
                            <div className="flex items-center">
                              <Clock className="h-4 w-4 mr-1" />
                              {schedule.time}
                            </div>
                            <div className="flex items-center">
                              <MapPin className="h-4 w-4 mr-1" />
                              {schedule.location}
                            </div>
                          </div>
                        </div>
                        <Badge
                          variant="secondary"
                          className="bg-blue-100 text-blue-800"
                        >
                          {schedule.participants}명
                        </Badge>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            ) : (
              <Card>
                <CardContent className="p-4 text-center text-gray-500">
                  해당 날짜에 일정이 없습니다
                </CardContent>
              </Card>
            )}
          </div>
        )}

        {/* 다가오는 일정 */}
        <div className="space-y-2">
          <h2 className="text-lg font-semibold">다가오는 일정</h2>
          {upcomingSchedules.length > 0 ? (
            <div className="space-y-3">
              {upcomingSchedules.map((schedule) => (
                <Card
                  key={schedule.scheduleId}
                  className="hover:shadow-md transition-shadow cursor-pointer"
                  onClick={() =>
                    router.push(
                      `/group/${schedule.gatheringId}/schedule/${schedule.scheduleId}`
                    )
                  }
                >
                  <CardContent className="p-4">
                    <div className="flex justify-between items-start">
                      <div className="space-y-2">
                        <div className="font-medium text-lg">
                          {schedule.scheduleTitle}
                        </div>
                        <div className="text-sm text-gray-500">
                          {schedule.gatheringName}
                        </div>
                        <div className="flex items-center text-sm text-gray-600 space-x-4">
                          <div className="flex items-center">
                            <Clock className="h-4 w-4 mr-1" />
                            {format(
                              new Date(schedule.scheduleStartTime),
                              "yyyy.MM.dd HH:mm"
                            )}
                          </div>
                          <div className="flex items-center">
                            <MapPin className="h-4 w-4 mr-1" />
                            {schedule.schedulePlace}
                          </div>
                        </div>
                      </div>
                      <Badge
                        variant="secondary"
                        className="bg-blue-100 text-blue-800"
                      >
                        {schedule.attendeeCount}명
                      </Badge>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="p-4 text-center text-gray-500">
                다가오는 일정이 없습니다
              </CardContent>
            </Card>
          )}
        </div>
      </main>
    </>
  );
}
