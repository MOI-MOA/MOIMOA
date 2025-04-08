"use client";
import React, { use, useState } from "react";
import { useRouter } from "next/navigation";
import { ArrowRight, AlertCircle, CalendarIcon } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Card, CardContent } from "@/components/ui/card";
import { toast } from "@/components/ui/use-toast";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Header } from "@/components/Header";

import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { toZonedTime } from "date-fns-tz";
import { Calendar } from "@/components/ui/calendar";
import { format } from "date-fns";
import { ko } from "date-fns/locale";
import { cn } from "@/lib/utils";
import axios from "axios";
import { authApi, publicApi } from "@/lib/api";
interface Member {
  name: string;
  balance: number;
  profileImage?: string;
}

interface MemberWithStatus {
  name: string;
  balance: number;
  avatar: string;
  insufficientAmount: number;
  status: "충분" | "부족";
}

interface ApiResponse {
  data: Member[];
}

export default function CreateSchedulePage({
  params,
}: {
  params: Promise<{ groupId: string }>;
}) {
  const router = useRouter();
  const { groupId } = use(params);
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    title: "",
    date: undefined as Date | undefined,
    time: "15:00",
    participants: "",
    budgetPerPerson: "",
    location: "",
    description: "",
    paybackDate: undefined as Date | undefined,
    paybackTime: "15:00",
    penaltyRate: "",
    scheduleAccountPw: "",
  });
  const [insufficientMembers, setInsufficientMembers] = useState<
    MemberWithStatus[]
  >([]);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleNext = () => {
    if (step < 3) {
      setStep((prev) => prev + 1);
    }
  };

  const handlePrevious = () => {
    if (step > 1) {
      setStep((prev) => prev - 1);
    }
  };

  const handleSubmit = async (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();

    const form = e.currentTarget.closest("form");
    if (form && !form.checkValidity()) {
      form.reportValidity();
      return;
    }

    setIsLoading(true);
    try {
      const scheduleDateTime = formData.date
        ? new Date(`${format(formData.date, "yyyy-MM-dd")}T${formData.time}:00`)
        : null;

      const paybackDateTime = formData.paybackDate
        ? new Date(
            `${format(formData.paybackDate, "yyyy-MM-dd")}T${
              formData.paybackTime
            }:00`
          )
        : null;

      const scheduleData = {
        scheduleTitle: formData.title,
        scheduleDetail: formData.description,
        schedulePlace: formData.location,
        scheduleStartTime: scheduleDateTime,
        perBudget: Number(formData.budgetPerPerson),
        penaltyApplyDate: paybackDateTime,
        penaltyRate: Number(formData.penaltyRate),
        scheduleAccountPw: formData.scheduleAccountPw,
      };
      console.log(scheduleData);
      const response = await authApi.post(
        `/api/v1/schedule/${groupId}`,
        scheduleData
      );

      toast({
        title: "일정 생성 완료",
        description: "새로운 일정이 성공적으로 생성되었습니다.",
      });
      router.push(`/group/${groupId}`);
    } catch (error: any) {
      console.error(error);
      if (error.response && error.response.status === 405) {
        toast({
          title: "일정 생성 불가",
          description:
            "잔액이 보증금 + 인당 일정 금액보다 부족해 일정 생성이 불가능합니다.",
          variant: "destructive",
        });
      } else {
        toast({
          title: "오류 발생",
          description: "일정 생성 중 문제가 발생했습니다. 다시 시도해주세요.",
          variant: "destructive",
        });
      }
    } finally {
      setIsLoading(false);
    }
  };

  const renderStep = () => {
    switch (step) {
      case 1:
        return (
          <>
            <div>
              <Label htmlFor="title">일정 제목</Label>
              <Input
                id="title"
                name="title"
                value={formData.title}
                onChange={handleInputChange}
                placeholder="일정 제목을 입력하세요"
                required
              />
            </div>
            <div>
              <Label>날짜 및 시간</Label>
              <div className="flex flex-col space-y-2">
                <Calendar
                  mode="single"
                  selected={formData.date}
                  onSelect={(date) =>
                    setFormData((prev) => ({ ...prev, date }))
                  }
                  className="rounded-md border [&_.rdp-caption]:text-sm [&_.rdp-cell]:text-sm [&_.rdp-head_cell]:text-sm [&_.rdp]:scale-75"
                />
                <Input
                  type="time"
                  value={formData.time}
                  onChange={(e) =>
                    setFormData((prev) => ({ ...prev, time: e.target.value }))
                  }
                  className="w-32"
                />
              </div>
            </div>
            <div>
              <Label htmlFor="participants">예상 참여 인원</Label>
              <Input
                id="participants"
                name="participants"
                type="number"
                value={formData.participants}
                onChange={handleInputChange}
                placeholder="참여 인원 수를 입력하세요"
                required
              />
            </div>
          </>
        );
      case 2:
        return (
          <>
            <div>
              <Label htmlFor="budgetPerPerson">인당 예산</Label>
              <div className="flex space-x-2">
                <Input
                  id="budgetPerPerson"
                  name="budgetPerPerson"
                  type="number"
                  value={formData.budgetPerPerson}
                  onChange={handleInputChange}
                  placeholder="인당 예산을 입력하세요"
                  required
                />
              </div>
            </div>
            <div>
              <Label htmlFor="location">장소</Label>
              <Input
                id="location"
                name="location"
                value={formData.location}
                onChange={handleInputChange}
                placeholder="모임 장소를 입력하세요"
                required
              />
            </div>
            <div>
              <Label htmlFor="penaltyRate">페널티 비율 (%)</Label>
              <Input
                id="penaltyRate"
                name="penaltyRate"
                type="number"
                min="0"
                max="100"
                value={formData.penaltyRate}
                onChange={handleInputChange}
                placeholder="페널티 비율을 입력하세요 (예: 10)"
                required
              />
            </div>
            <div>
              <Label htmlFor="scheduleAccountPw">정산 비밀번호</Label>
              <Input
                id="scheduleAccountPw"
                name="scheduleAccountPw"
                type="password"
                maxLength={4}
                value={formData.scheduleAccountPw}
                onChange={handleInputChange}
                placeholder="4자리 숫자를 입력하세요"
                pattern="[0-9]{4}"
                required
              />
            </div>
          </>
        );
      case 3:
        return (
          <>
            <div>
              <Label htmlFor="description">상세 내용</Label>
              <Input
                id="description"
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                placeholder="상세 내용을 입력하세요"
              />
            </div>
            <div>
              <Label>페이백 적용 날짜 및 시간</Label>
              <div className="flex flex-col space-y-2">
                <Calendar
                  mode="single"
                  selected={formData.paybackDate}
                  onSelect={(date) =>
                    setFormData((prev) => ({ ...prev, paybackDate: date }))
                  }
                  className="rounded-md border [&_.rdp-caption]:text-sm [&_.rdp-cell]:text-sm [&_.rdp-head_cell]:text-sm [&_.rdp]:scale-75"
                />
                <Input
                  type="time"
                  value={formData.paybackTime}
                  onChange={(e) =>
                    setFormData((prev) => ({
                      ...prev,
                      paybackTime: e.target.value,
                    }))
                  }
                  className="w-32"
                />
              </div>
            </div>
          </>
        );
      default:
        return null;
    }
  };

  return (
    <>
      <Header title="새 일정 만들기" showBackButton />
      <main className="flex-1 overflow-auto p-4 pb-16">
        <Card>
          <CardContent className="p-6">
            <div className="mb-6 text-center">
              <h2 className="text-xl font-semibold">Step {step} of 3</h2>
              <div className="w-full bg-gray-200 rounded-full h-2.5 mt-2">
                <div
                  className="bg-blue-600 h-2.5 rounded-full"
                  style={{ width: `${(step / 3) * 100}%` }}
                ></div>
              </div>
            </div>
            <form onSubmit={(e) => e.preventDefault()} className="space-y-4">
              {renderStep()}
              <div className="flex justify-between mt-6">
                {step > 1 && (
                  <Button type="button" onClick={handlePrevious}>
                    이전
                  </Button>
                )}
                {step < 3 ? (
                  <Button
                    type="button"
                    onClick={handleNext}
                    className="ml-auto"
                  >
                    다음
                    <ArrowRight className="h-4 w-4 ml-2" />
                  </Button>
                ) : (
                  <Button
                    type="button"
                    onClick={handleSubmit}
                    className="ml-auto bg-blue-500 hover:bg-blue-600 text-white"
                    disabled={isLoading}
                  >
                    {isLoading ? "처리 중..." : "일정 만들기"}
                  </Button>
                )}
              </div>
            </form>
          </CardContent>
        </Card>
      </main>

      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>회원별 예산 현황</DialogTitle>
          </DialogHeader>
          <div className="mt-4">
            {insufficientMembers.map((member, index) => (
              <div
                key={index}
                className="flex items-center justify-between py-2 border-b last:border-b-0"
              >
                <div className="flex items-center">
                  <Avatar className="h-8 w-8 mr-2">
                    <AvatarImage src={member.avatar} />
                    <AvatarFallback>{member.name.slice(0, 2)}</AvatarFallback>
                  </Avatar>
                  <span>{member.name}</span>
                </div>
                <div className="flex flex-col items-end">
                  <span className="text-sm">
                    {member.balance.toLocaleString()}원
                  </span>
                  {member.status === "부족" ? (
                    <Badge variant="destructive" className="mt-1">
                      <AlertCircle className="h-3 w-3 mr-1" />
                      {member.insufficientAmount.toLocaleString()}원 부족
                    </Badge>
                  ) : (
                    <Badge
                      variant="secondary"
                      className="mt-1 bg-green-100 text-green-800"
                    >
                      충분
                    </Badge>
                  )}
                </div>
              </div>
            ))}
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
}
