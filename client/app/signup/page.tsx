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
import axios from "axios";
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

  const renderStepContent = () => {
    switch (step) {
      case 1:
        return (
          <div className="space-y-4">
            <p>회원가입을 위해 본인인증이 필요합니다.</p>
            <Button onClick={handleVerification} disabled={isVerified}>
              {isVerified ? "인증 완료" : "본인인증"}
            </Button>
          </div>
        );
      case 2:
        return (
          <>
            <div className="space-y-2">
              <Label htmlFor="name">이름</Label>
              <Input
                id="name"
                name="name"
                type="text"
                placeholder="홍길동"
                required
                minLength={2}
                maxLength={20}
                value={formData.name}
                onChange={handleInputChange}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="email">이메일</Label>
              <Input
                id="email"
                name="email"
                type="email"
                placeholder="example@example.com"
                required
                value={formData.email}
                onChange={handleInputChange}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="birth">생년월일</Label>
              <Input
                id="birth"
                name="birth"
                type="date"
                required
                value={formData.birth}
                onChange={handleInputChange}
                max="9999-12-31"
                min="1900-01-01"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="password">비밀번호</Label>
              <Input
                id="password"
                name="password"
                type="password"
                required
                minLength={4}
                maxLength={20}
                value={formData.password}
                onChange={handleInputChange}
                pattern="^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{4,20}$"
              />
              <p className="text-sm text-gray-500">
                4~20자리수, 영문 대소문자, 숫자, 특수문자 1개 이상 포함
              </p>
            </div>
            <div className="space-y-2">
              <Label htmlFor="confirmPassword">비밀번호 확인</Label>
              <Input
                id="confirmPassword"
                name="confirmPassword"
                type="password"
                required
                value={formData.confirmPassword}
                onChange={handleInputChange}
              />
            </div>
          </>
        );
      case 3:
        return (
          <div className="space-y-2">
            <Label htmlFor="personalAccountPW">개인계좌 비밀번호 (6자리)</Label>
            <Input
              id="personalAccountPW"
              name="personalAccountPW"
              type="password"
              maxLength={6}
              required
              value={formData.personalAccountPW}
              onChange={(e) => {
                const value = e.target.value.replace(/[^0-9]/g, "");
                setFormData((prev) => ({
                  ...prev,
                  personalAccountPW: value,
                }));
              }}
              pattern="[0-9]{6}"
            />
            <p className="text-sm text-gray-500">
              {formData.personalAccountPW.length === 6
                ? "다음 단계로 진행할 수 있습니다."
                : "6자리 숫자를 입력해주세요."}
            </p>
          </div>
        );
      case 4:
        return (
          <div className="space-y-2">
            <Label htmlFor="confirmPersonalAccountPW">
              개인계좌 비밀번호 확인
            </Label>
            <Input
              id="confirmPersonalAccountPW"
              name="confirmPersonalAccountPW"
              type="password"
              maxLength={6}
              required
              value={formData.confirmPersonalAccountPW}
              onChange={(e) => {
                const value = e.target.value.replace(/[^0-9]/g, "");
                setFormData((prev) => ({
                  ...prev,
                  confirmPersonalAccountPW: value,
                }));
              }}
              pattern="[0-9]{6}"
            />
          </div>
        );
    }
  };

  return (
    <div className="min-h-screen flex flex-col">
      <Toaster />
      <Header title="회원가입" showBackButton />
      <main className="flex-1 overflow-auto p-4 flex items-center justify-center">
        <Card className="w-full max-w-md">
          <form onSubmit={handleSubmit}>
            <CardHeader>
              <CardTitle>회원가입 - 단계 {step}/4</CardTitle>
              <CardDescription>
                새 계정을 만들어 서비스를 이용하세요.
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              {/* Progress bar */}
              <div className="w-full bg-gray-200 rounded-full h-2.5">
                <div
                  className="bg-blue-600 h-2.5 rounded-full"
                  style={{ width: `${(step / 4) * 100}%` }}
                ></div>
              </div>
              {renderStepContent()}
            </CardContent>
            <CardFooter className="flex justify-between">
              {step > 1 && step !== 4 && (
                <Button
                  type="button"
                  onClick={handlePrevStep}
                  variant="outline"
                >
                  이전
                </Button>
              )}
              {step < 4 ? (
                <Button
                  type="button"
                  onClick={handleNextStep}
                  disabled={step === 1 && !isVerified}
                  className="ml-auto"
                >
                  다음
                </Button>
              ) : (
                <Button
                  type="submit"
                  className="ml-auto"
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
