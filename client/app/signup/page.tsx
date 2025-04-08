"use client";

import type React from "react";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Header } from "@/components/Header";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { toast } from "@/components/ui/use-toast";
import { Toaster } from "@/components/ui/toaster";
import {
  User,
  ChevronRight,
  ChevronLeft,
  Mail,
  Shield,
  Key,
  LockKeyhole,
  CalendarDays
} from "lucide-react";
import { publicApi, authApi } from "@/lib/api";

export default function SignUpPage() {
  const router = useRouter();
  const [step, setStep] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [isVerified, setIsVerified] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
    birth: "",
    personalAccountPW: "",
    confirmPersonalAccountPW: "",
  });

  // 비밀번호 유효성 검사
  const validatePassword = (password: string) => {
    const passwordRegex =
      /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{4,20}$/;
    return passwordRegex.test(password);
  };

  // PIN 번호 유효성 검사
  const validatePin = (pin: string) => {
    return /^\d{6}$/.test(pin);
  };

  useEffect(() => {
    // 개인계좌 비밀번호 입력 완료 시 다음 단계로 이동
    if (formData.personalAccountPW.length === 6 && step === 3) {
      toast({
        title: "개인계좌 비밀번호 입력 완료",
        description: "개인계좌 비밀번호를 한 번 더 입력해주세요.",
      });
    }

    // 개인계좌 비밀번호 확인 완료 시
    if (formData.confirmPersonalAccountPW.length === 6 && step === 4) {
      if (formData.confirmPersonalAccountPW !== formData.personalAccountPW) {
        toast({
          title: "개인계좌 비밀번호 불일치",
          description:
            "개인계좌 비밀번호가 일치하지 않습니다. 다시 입력해주세요.",
          variant: "destructive",
        });
        setFormData((prev) => ({
          ...prev,
          personalAccountPW: "",
          confirmPersonalAccountPW: "",
        }));
      } else {
        toast({
          title: "개인계좌 비밀번호 확인 완료",
          description: "개인계좌 비밀번호가 성공적으로 설정되었습니다.",
        });
      }
    }
  }, [formData.personalAccountPW, formData.confirmPersonalAccountPW, step]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleVerification = () => {
    setIsVerified(true);
    toast({
      title: "본인인증 완료",
      description: "본인인증이 성공적으로 완료되었습니다.",
    });
  };

  const handleNextStep = () => {
    if (step < 4) {
      // 각 단계별 유효성 검사
      switch (step) {
        case 1:
          // 이메일 인증 확인
          if (!isVerified) {
            toast({
              title: "이메일 인증 필요",
              description: "이메일 인증을 완료해주세요.",
              variant: "destructive",
            });
            return;
          }
          break;
        case 2:
          // 비밀번호 유효성 검사
          if (!validatePassword(formData.password)) {
            toast({
              title: "비밀번호 형식 오류",
              description:
                "비밀번호는 4~20자리수여야 합니다. 영문 대소문자, 숫자, 특수문자를 1개 이상 포함해야 합니다.",
              variant: "destructive",
            });
            return;
          }
          // 비밀번호 확인 일치 여부
          if (formData.password !== formData.confirmPassword) {
            toast({
              title: "비밀번호 불일치",
              description: "비밀번호와 비밀번호 확인이 일치하지 않습니다.",
              variant: "destructive",
            });
            return;
          }
          break;
        case 3:
          // 개인계좌 비밀번호 유효성 검사
          if (!validatePin(formData.personalAccountPW)) {
            toast({
              title: "개인계좌 비밀번호 형식 오류",
              description: "개인계좌 비밀번호는 6자리 숫자여야 합니다.",
              variant: "destructive",
            });
            return;
          }
          if (formData.personalAccountPW.length !== 6) {
            toast({
              title: "개인계좌 비밀번호 미입력",
              description: "개인계좌 비밀번호를 6자리 입력해주세요.",
              variant: "destructive",
            });
            return;
          }
          // 다음 단계로 넘어갈 때 개인계좌 비밀번호 확인 필드 초기화
          setFormData((prev) => ({
            ...prev,
            confirmPersonalAccountPW: "",
          }));
          break;
      }
      setStep(step + 1);
    }
  };

  const handlePrevStep = () => {
    if (step > 1) {
      // 이전 단계로 돌아갈 때 개인계좌 비밀번호 관련 필드 초기화
      if (step === 4) {
        setFormData((prev) => ({
          ...prev,
          personalAccountPW: "",
          confirmPersonalAccountPW: "",
        }));
      }
      setStep(step - 1);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      // 마지막 단계에서 개인계좌 비밀번호 확인 검증
      if (formData.personalAccountPW !== formData.confirmPersonalAccountPW) {
        toast({
          title: "개인계좌 비밀번호 불일치",
          description: "개인계좌 비밀번호가 일치하지 않습니다.",
          variant: "destructive",
        });
        setIsLoading(false);
        return;
      }

      const response = await authApi.post("/api/v1/signup", {
        name: formData.name,
        email: formData.email,
        password: formData.password,
        birth: formData.birth,
        personalAccountPW: parseInt(formData.personalAccountPW),
      });

      toast({
        title: "회원가입 완료",
        description: "회원가입이 완료되었습니다. 로그인해주세요.",
      });

      router.push("/login");
    } catch (error: any) {
      toast({
        title: "회원가입 실패",
        description:
          error.response?.data?.message || "회원가입 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  const getStepIcon = (currentStep: number) => {
    switch(currentStep) {
      case 1: return <Shield className="h-8 w-8 text-blue-600" />;
      case 2: return <User className="h-8 w-8 text-blue-600" />;
      case 3: return <Key className="h-8 w-8 text-blue-600" />;
      case 4: return <LockKeyhole className="h-8 w-8 text-blue-600" />;
      default: return <User className="h-8 w-8 text-blue-600" />;
    }
  };

  const renderStepContent = () => {
    switch (step) {
      case 1:
        return (
          <div className="space-y-6">
            <div className="text-center mb-6">
              <div className="bg-blue-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <Shield className="h-8 w-8 text-blue-600" />
              </div>
              <p className="text-slate-600">회원가입을 위한 본인인증이 필요합니다.</p>
            </div>
            <Button 
              onClick={handleVerification} 
              disabled={isVerified} 
              className={`w-full rounded-xl py-6 ${isVerified ? 'bg-green-600 hover:bg-green-700' : 'bg-blue-600 hover:bg-blue-700'}`}
            >
              {isVerified ? "인증 완료 ✓" : "본인인증 진행하기"}
            </Button>
          </div>
        );
      case 2:
        return (
          <>
            <div className="space-y-4">
              <div className="relative">
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <User className="h-5 w-5 text-slate-400" />
                </div>
                <Input
                  id="name"
                  name="name"
                  type="text"
                  placeholder="이름을 입력하세요"
                  required
                  minLength={2}
                  maxLength={20}
                  value={formData.name}
                  onChange={handleInputChange}
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
              </div>
              
              <div className="relative">
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <Mail className="h-5 w-5 text-slate-400" />
                </div>
                <Input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="이메일 주소"
                  required
                  value={formData.email}
                  onChange={handleInputChange}
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
              </div>
              
              <div className="relative">
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <CalendarDays className="h-5 w-5 text-slate-400" />
                </div>
                <Input
                  id="birth"
                  name="birth"
                  type="date"
                  required
                  value={formData.birth}
                  onChange={handleInputChange}
                  max="9999-12-31"
                  min="1900-01-01"
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
              </div>
              
              <div className="relative">
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <Key className="h-5 w-5 text-slate-400" />
                </div>
                <Input
                  id="password"
                  name="password"
                  type="password"
                  placeholder="비밀번호"
                  required
                  minLength={4}
                  maxLength={20}
                  value={formData.password}
                  onChange={handleInputChange}
                  pattern="^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{4,20}$"
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
              </div>
              <p className="text-sm text-slate-500 pl-3">
                4~20자리수, 영문 대소문자, 숫자, 특수문자 1개 이상 포함
              </p>
              
              <div className="relative">
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <LockKeyhole className="h-5 w-5 text-slate-400" />
                </div>
                <Input
                  id="confirmPassword"
                  name="confirmPassword"
                  type="password"
                  placeholder="비밀번호 확인"
                  required
                  value={formData.confirmPassword}
                  onChange={handleInputChange}
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
              </div>
            </div>
          </>
        );
      case 3:
        return (
          <div className="space-y-4">
            <div className="text-center mb-6">
              <div className="bg-blue-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <Key className="h-8 w-8 text-blue-600" />
              </div>
              <h3 className="text-lg font-semibold text-slate-800 mb-1">개인계좌 비밀번호 설정</h3>
              <p className="text-slate-600">개인 계좌 접근을 위한 6자리 PIN 번호를 설정해주세요.</p>
            </div>
            
            <div className="bg-slate-50 p-4 rounded-xl">
              <Label htmlFor="personalAccountPW" className="text-slate-700 font-medium mb-2 block">
                개인계좌 비밀번호 (6자리)
              </Label>
              <Input
                id="personalAccountPW"
                name="personalAccountPW"
                type="password"
                maxLength={6}
                required
                value={formData.personalAccountPW}
                placeholder="6자리 숫자를 입력하세요"
                onChange={(e) => {
                  const value = e.target.value.replace(/[^0-9]/g, "");
                  setFormData((prev) => ({
                    ...prev,
                    personalAccountPW: value,
                  }));
                }}
                pattern="[0-9]{6}"
                className="text-center py-6 text-lg rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
              />
              <div className="mt-3 flex items-center justify-center">
                <div className={`w-8 h-1.5 rounded mx-1 ${formData.personalAccountPW.length >= 1 ? 'bg-blue-500' : 'bg-slate-200'}`}></div>
                <div className={`w-8 h-1.5 rounded mx-1 ${formData.personalAccountPW.length >= 2 ? 'bg-blue-500' : 'bg-slate-200'}`}></div>
                <div className={`w-8 h-1.5 rounded mx-1 ${formData.personalAccountPW.length >= 3 ? 'bg-blue-500' : 'bg-slate-200'}`}></div>
                <div className={`w-8 h-1.5 rounded mx-1 ${formData.personalAccountPW.length >= 4 ? 'bg-blue-500' : 'bg-slate-200'}`}></div>
                <div className={`w-8 h-1.5 rounded mx-1 ${formData.personalAccountPW.length >= 5 ? 'bg-blue-500' : 'bg-slate-200'}`}></div>
                <div className={`w-8 h-1.5 rounded mx-1 ${formData.personalAccountPW.length >= 6 ? 'bg-blue-500' : 'bg-slate-200'}`}></div>
              </div>
            </div>
          </div>
        );
      case 4:
        return (
          <div className="space-y-4">
            <div className="text-center mb-6">
              <div className="bg-blue-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <LockKeyhole className="h-8 w-8 text-blue-600" />
              </div>
              <h3 className="text-lg font-semibold text-slate-800 mb-1">개인계좌 비밀번호 확인</h3>
              <p className="text-slate-600">동일한 PIN 번호를 한 번 더 입력해주세요.</p>
            </div>
            
            <div className="bg-slate-50 p-4 rounded-xl">
              <Label htmlFor="confirmPersonalAccountPW" className="text-slate-700 font-medium mb-2 block">
                개인계좌 비밀번호 확인
              </Label>
              <Input
                id="confirmPersonalAccountPW"
                name="confirmPersonalAccountPW"
                type="password"
                maxLength={6}
                required
                placeholder="6자리 숫자를 다시 입력하세요"
                value={formData.confirmPersonalAccountPW}
                onChange={(e) => {
                  const value = e.target.value.replace(/[^0-9]/g, "");
                  setFormData((prev) => ({
                    ...prev,
                    confirmPersonalAccountPW: value,
                  }));
                }}
                pattern="[0-9]{6}"
                className="text-center py-6 text-lg rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
              />
              <div className="mt-3 flex items-center justify-center">
                <div className={`w-8 h-1.5 rounded mx-1 ${formData.confirmPersonalAccountPW.length >= 1 ? 'bg-blue-500' : 'bg-slate-200'}`}></div>
                <div className={`w-8 h-1.5 rounded mx-1 ${formData.confirmPersonalAccountPW.length >= 2 ? 'bg-blue-500' : 'bg-slate-200'}`}></div>
                <div className={`w-8 h-1.5 rounded mx-1 ${formData.confirmPersonalAccountPW.length >= 3 ? 'bg-blue-500' : 'bg-slate-200'}`}></div>
                <div className={`w-8 h-1.5 rounded mx-1 ${formData.confirmPersonalAccountPW.length >= 4 ? 'bg-blue-500' : 'bg-slate-200'}`}></div>
                <div className={`w-8 h-1.5 rounded mx-1 ${formData.confirmPersonalAccountPW.length >= 5 ? 'bg-blue-500' : 'bg-slate-200'}`}></div>
                <div className={`w-8 h-1.5 rounded mx-1 ${formData.confirmPersonalAccountPW.length >= 6 ? 'bg-blue-500' : 'bg-slate-200'}`}></div>
              </div>
            </div>
          </div>
        );
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-slate-50">
      <Toaster />
      <Header title="회원가입" showBackButton />
      <main className="flex-1 overflow-auto p-4 flex items-center justify-center">
        <Card className="w-full max-w-md border-0 shadow-sm rounded-xl overflow-hidden">
          <form onSubmit={handleSubmit}>
            <CardHeader className="pb-3">
              <div className="flex items-center">
                <span className="inline-flex items-center justify-center w-8 h-8 bg-blue-100 text-blue-700 rounded-full mr-2 text-sm font-bold">
                  {step}
                </span>
                <CardTitle className="text-xl text-slate-800">회원가입</CardTitle>
              </div>
              <CardDescription className="text-slate-500">
                {step}/4단계 - {
                  step === 1 ? '본인인증' : 
                  step === 2 ? '개인정보 입력' : 
                  step === 3 ? '개인계좌 비밀번호 설정' : 
                  '개인계좌 비밀번호 확인'
                }
              </CardDescription>
            </CardHeader>
            
            <CardContent className="space-y-4">
              {/* Progress bar */}
              <div className="w-full bg-slate-200 rounded-full h-2">
                <div
                  className="bg-blue-600 h-2 rounded-full transition-all duration-300 ease-in-out"
                  style={{ width: `${(step / 4) * 100}%` }}
                ></div>
              </div>
              {renderStepContent()}
            </CardContent>
            
            <CardFooter className="flex justify-between pt-4">
              {step > 1 ? (
                <Button
                  type="button"
                  onClick={handlePrevStep}
                  variant="outline"
                  className="rounded-xl border-slate-200 text-slate-700 hover:bg-slate-100"
                >
                  <ChevronLeft className="h-4 w-4 mr-2" />
                  이전
                </Button>
              ) : (
                <div></div>
              )}
              
              {step < 4 ? (
                <Button
                  type="button"
                  onClick={handleNextStep}
                  disabled={step === 1 && !isVerified}
                  className="ml-auto rounded-xl bg-blue-600 hover:bg-blue-700"
                >
                  다음
                  <ChevronRight className="h-4 w-4 ml-2" />
                </Button>
              ) : (
                <Button
                  type="submit"
                  className="ml-auto rounded-xl bg-blue-600 hover:bg-blue-700"
                  disabled={
                    isLoading ||
                    formData.confirmPersonalAccountPW.length !== 6 ||
                    formData.personalAccountPW !==
                      formData.confirmPersonalAccountPW
                  }
                >
                  {isLoading ? "처리 중..." : "가입 완료"}
                </Button>
              )}
            </CardFooter>
          </form>
        </Card>
      </main>
    </div>
  );
}
