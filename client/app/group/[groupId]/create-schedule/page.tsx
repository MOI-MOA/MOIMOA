"use client";
import React, { use, useState } from "react";
import { useRouter } from "next/navigation";
import { 
  ArrowRight, 
  ArrowLeft,
  AlertCircle, 
  CalendarIcon,
  Clock,
  Users,
  DollarSign,
  MapPin,
  Percent,
  Lock,
  MessageSquare,
  RefreshCw,
  Calendar as CalendarIcon2
} from "lucide-react";
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
import { Calendar } from "@/components/ui/calendar";
import { format } from "date-fns";
import { ko } from "date-fns/locale";
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
    
    // 숫자 입력 필드에 대한 검증 추가
    if (["participants", "budgetPerPerson", "penaltyRate"].includes(name)) {
      // 빈 값은 허용 (사용자가 지우고 다시 입력할 수 있도록)
      if (value === "") {
        setFormData((prev) => ({ ...prev, [name]: value }));
        return;
      }
      
      const numValue = parseFloat(value);
      
      // 음수 입력 방지
      if (numValue < 0) {
        return; // 유효하지 않은 값이면 상태 업데이트하지 않음
      }
      
      // penaltyRate 특별 처리 (0-100 사이 값만)
      if (name === "penaltyRate" && numValue > 100) {
        return;
      }
    }
    
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

  const getStepTitle = () => {
    switch (step) {
      case 1:
        return "기본 정보";
      case 2:
        return "예산 및 장소";
      case 3:
        return "상세 정보";
      default:
        return "";
    }
  };

  const getStepIcon = () => {
    switch (step) {
      case 1:
        return <CalendarIcon2 className="h-5 w-5 mr-2 text-blue-600" />;
      case 2:
        return <DollarSign className="h-5 w-5 mr-2 text-blue-600" />;
      case 3:
        return <MessageSquare className="h-5 w-5 mr-2 text-blue-600" />;
      default:
        return null;
    }
  };

  const renderStep = () => {
    switch (step) {
      case 1:
        return (
          <>
            <div className="space-y-2">
              <Label htmlFor="title" className="text-slate-700 font-medium flex items-center">
                <MessageSquare className="h-4 w-4 mr-1.5 text-slate-500" />
                일정 제목
              </Label>
              <div className="relative">
                <Input
                  id="title"
                  name="title"
                  value={formData.title}
                  onChange={handleInputChange}
                  placeholder="일정 제목을 입력하세요"
                  required
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <MessageSquare className="h-5 w-5 text-slate-400" />
                </div>
              </div>
            </div>

            <div className="space-y-2">
              <Label className="text-slate-700 font-medium flex items-center">
                <CalendarIcon className="h-4 w-4 mr-1.5 text-slate-500" />
                날짜 및 시간
              </Label>
              <div className="space-y-4">
                <Card className="border border-slate-200 rounded-xl overflow-hidden">
                  <div className="p-2">
                    <Calendar
                      mode="single"
                      selected={formData.date}
                      onSelect={(date) => setFormData((prev) => ({ ...prev, date }))}
                      className="rounded-xl [&_.rdp-caption]:px-4 [&_.rdp-caption]:py-2 [&_.rdp-caption]:text-sm [&_.rdp-cell]:text-sm [&_.rdp-head_cell]:text-sm [&_.rdp]:scale-75"
                    />
                  </div>
                </Card>
                <div className="relative">
                  <Input
                    type="time"
                    value={formData.time}
                    onChange={(e) => setFormData((prev) => ({ ...prev, time: e.target.value }))}
                    className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                  />
                  <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                    <Clock className="h-5 w-5 text-slate-400" />
                  </div>
                </div>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="participants" className="text-slate-700 font-medium flex items-center">
                <Users className="h-4 w-4 mr-1.5 text-slate-500" />
                예상 참여 인원
              </Label>
              <div className="relative">
                <Input
                  id="participants"
                  name="participants"
                  type="number"
                  min="1"
                  value={formData.participants}
                  onChange={handleInputChange}
                  onWheel={(e) => e.currentTarget.blur()}
                  placeholder="참여 인원 수를 입력하세요"
                  required
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <Users className="h-5 w-5 text-slate-400" />
                </div>
                <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                  <span className="text-slate-400">명</span>
                </div>
              </div>
            </div>
          </>
        );
      case 2:
        return (
          <>
            <div className="space-y-2">
              <Label htmlFor="budgetPerPerson" className="text-slate-700 font-medium flex items-center">
                <DollarSign className="h-4 w-4 mr-1.5 text-slate-500" />
                인당 예산
              </Label>
              <div className="relative">
                <Input
                  id="budgetPerPerson"
                  name="budgetPerPerson"
                  type="number"
                  min="0"
                  value={formData.budgetPerPerson}
                  onChange={handleInputChange}
                  onWheel={(e) => e.currentTarget.blur()}
                  placeholder="인당 예산을 입력하세요"
                  required
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <DollarSign className="h-5 w-5 text-slate-400" />
                </div>
                <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                  <span className="text-slate-400">원</span>
                </div>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="location" className="text-slate-700 font-medium flex items-center">
                <MapPin className="h-4 w-4 mr-1.5 text-slate-500" />
                장소
              </Label>
              <div className="relative">
                <Input
                  id="location"
                  name="location"
                  value={formData.location}
                  onChange={handleInputChange}
                  placeholder="모임 장소를 입력하세요"
                  required
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <MapPin className="h-5 w-5 text-slate-400" />
                </div>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="penaltyRate" className="text-slate-700 font-medium flex items-center">
                <Percent className="h-4 w-4 mr-1.5 text-slate-500" />
                페널티 비율
              </Label>
              <div className="relative">
                <Input
                  id="penaltyRate"
                  name="penaltyRate"
                  type="number"
                  min="0"
                  max="100"
                  value={formData.penaltyRate}
                  onChange={handleInputChange}
                  onWheel={(e) => e.currentTarget.blur()}
                  placeholder="페널티 비율을 입력하세요 (예: 10)"
                  required
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <Percent className="h-5 w-5 text-slate-400" />
                </div>
                <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                  <span className="text-slate-400">%</span>
                </div>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="scheduleAccountPw" className="text-slate-700 font-medium flex items-center">
                <Lock className="h-4 w-4 mr-1.5 text-slate-500" />
                정산 비밀번호
              </Label>
              <div className="relative">
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
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <Lock className="h-5 w-5 text-slate-400" />
                </div>
              </div>
              <p className="text-sm text-slate-500 flex items-center pl-1">
                <AlertCircle className="h-3.5 w-3.5 mr-1 text-blue-500" />
                정산 시 필요한 4자리 비밀번호를 설정해주세요.
              </p>
            </div>
          </>
        );
      case 3:
        return (
          <>
            <div className="space-y-2">
              <Label htmlFor="description" className="text-slate-700 font-medium flex items-center">
                <MessageSquare className="h-4 w-4 mr-1.5 text-slate-500" />
                상세 내용
              </Label>
              <div className="relative">
                <Input
                  id="description"
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  placeholder="상세 내용을 입력하세요"
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <MessageSquare className="h-5 w-5 text-slate-400" />
                </div>
              </div>
            </div>

            <div className="space-y-2">
              <Label className="text-slate-700 font-medium flex items-center">
                <CalendarIcon className="h-4 w-4 mr-1.5 text-slate-500" />
                페이백 적용 날짜 및 시간
              </Label>
              <div className="space-y-4">
                <Card className="border border-slate-200 rounded-xl overflow-hidden">
                  <div className="p-2">
                    <Calendar
                      mode="single"
                      selected={formData.paybackDate}
                      onSelect={(date) => setFormData((prev) => ({ ...prev, paybackDate: date }))}
                      className="rounded-xl [&_.rdp-caption]:px-4 [&_.rdp-caption]:py-2 [&_.rdp-caption]:text-sm [&_.rdp-cell]:text-sm [&_.rdp-head_cell]:text-sm [&_.rdp]:scale-75"
                    />
                  </div>
                </Card>
                <div className="relative">
                  <Input
                    type="time"
                    value={formData.paybackTime}
                    onChange={(e) => setFormData((prev) => ({ ...prev, paybackTime: e.target.value }))}
                    className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                  />
                  <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                    <Clock className="h-5 w-5 text-slate-400" />
                  </div>
                </div>
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
      <main className="flex-1 overflow-auto p-4 pb-16 bg-slate-50">
        <div className="max-w-lg mx-auto">
          <h2 className="text-xl font-semibold text-slate-800 flex items-center mb-4">
            {getStepIcon()}
            {getStepTitle()}
          </h2>

          <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
            <CardContent className="p-6">
              <div className="mb-6">
                <div className="flex justify-between text-sm font-medium text-slate-600 mb-2">
                  <span>단계 {step}/3</span>
                  <span>{getStepTitle()}</span>
                </div>
                <div className="w-full bg-slate-200 rounded-full h-2">
                  <div
                    className="bg-blue-600 h-2 rounded-full transition-all duration-300"
                    style={{ width: `${(step / 3) * 100}%` }}
                  ></div>
                </div>
              </div>

              <form onSubmit={(e) => e.preventDefault()} className="space-y-5">
                {renderStep()}
                
                <div className="flex space-x-3 pt-3">
                  {step > 1 && (
                    <Button
                      type="button"
                      onClick={handlePrevious}
                      className="flex-1 rounded-xl border-slate-200 hover:bg-slate-100 text-slate-700"
                      variant="outline"
                    >
                      <ArrowLeft className="h-4 w-4 mr-2" />
                      이전
                    </Button>
                  )}
                  
                  {step < 3 ? (
                    <Button
                      type="button"
                      onClick={handleNext}
                      className={`${step > 1 ? 'flex-1' : 'w-full'} rounded-xl bg-blue-600 hover:bg-blue-700`}
                    >
                      <div className="flex items-center justify-center">
                        다음
                        <ArrowRight className="h-4 w-4 ml-2" />
                      </div>
                    </Button>
                  ) : (
                    <Button
                      type="button"
                      onClick={handleSubmit}
                      className={`${step > 1 ? 'flex-1' : 'w-full'} rounded-xl bg-blue-600 hover:bg-blue-700`}
                      disabled={isLoading}
                    >
                      {isLoading ? (
                        <div className="flex items-center justify-center">
                          <RefreshCw className="h-4 w-4 mr-2 animate-spin" />
                          처리 중...
                        </div>
                      ) : (
                        <div className="flex items-center justify-center">
                          일정 만들기
                          <ArrowRight className="h-4 w-4 ml-2" />
                        </div>
                      )}
                    </Button>
                  )}
                </div>
              </form>
            </CardContent>
          </Card>

          <div className="bg-blue-50 p-4 rounded-xl border border-blue-100 mt-4">
            <div className="flex">
              <div className="bg-blue-100 p-2 rounded-full mr-3 flex-shrink-0">
                <AlertCircle className="h-5 w-5 text-blue-600" />
              </div>
              <div>
                <h3 className="text-sm font-medium text-blue-800 mb-1">일정 생성 안내</h3>
                <p className="text-xs text-blue-700">
                  일정 생성 시 설정한 인당 예산은 참여자들의 가용 금액에서 자동으로 차감됩니다.
                  페이백 적용 날짜 이후에는 설정한 비율만큼 페널티가 적용됩니다.
                </p>
              </div>
            </div>
          </div>
        </div>
      </main>

      {/* 회원별 예산 현황 다이얼로그 */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="sm:max-w-md rounded-xl">
          <DialogHeader>
            <DialogTitle className="flex items-center text-slate-800">
              <DollarSign className="h-5 w-5 mr-2 text-blue-600" />
              회원별 예산 현황
            </DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            {insufficientMembers.map((member, index) => (
              <div
                key={index}
                className="flex items-center justify-between p-3 rounded-xl border border-slate-200"
              >
                <div className="flex items-center">
                  <Avatar className="h-10 w-10 border-2 border-slate-100">
                    <AvatarImage src={member.avatar} />
                    <AvatarFallback className="bg-slate-50 text-slate-600">
                      {member.name.slice(0, 2)}
                    </AvatarFallback>
                  </Avatar>
                  <span className="ml-3 font-medium text-slate-700">{member.name}</span>
                </div>
                <div className="flex flex-col items-end">
                  <span className="text-sm text-slate-600">
                    {member.balance.toLocaleString()}원
                  </span>
                  {member.status === "부족" ? (
                    <Badge variant="destructive" className="mt-1 bg-red-50 text-red-600 border-red-200">
                      <AlertCircle className="h-3 w-3 mr-1" />
                      {member.insufficientAmount.toLocaleString()}원 부족
                    </Badge>
                  ) : (
                    <Badge className="mt-1 bg-green-50 text-green-600 border-green-200">
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

