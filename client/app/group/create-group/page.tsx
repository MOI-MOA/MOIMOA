"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { ArrowRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Card, CardContent } from "@/components/ui/card";
import { toast } from "@/components/ui/use-toast";
import { Header } from "@/components/Header";
import { publicApi, authApi } from "@/lib/api";
interface GatheringData {
  gatheringTitle: string;
  gatheringIntroduction: string;
  memberCount: number;
  basicFee: number;
  accountNumber?: string;
  bankName?: string;
  paybackPercent: number;
}

interface ApiResponse {
  httpStatus: number;
  message: string;
  datas: GatheringData[];
}

export default function CreateGroupPage() {
  const router = useRouter();
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    gatheringTitle: "",
    gatheringIntroduction: "",
    memberCount: "",
    basicFee: "",
    accountNumber: "",
    bankName: "싸피 은행",
    pinNumber: "",
    paybackPercent: "",
    depositDate: "",
    gatheringDeposit: "",
    groupName: "",
    amount: "",
    day: "",
    account: "",
    deposit: "",
  });
  const [isLoading, setIsLoading] = useState(false);
  const [isCreatingAccount, setIsCreatingAccount] = useState(false);
  const [showPinInput, setShowPinInput] = useState(false);
  const [isPinDialogOpen, setIsPinDialogOpen] = useState(false);
  const [transferStatus, setTransferStatus] = useState("");

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;

    // monthlyDepositDay 입력값 검증
    if (name === "monthlyDepositDay") {
      const numValue = parseInt(value);
      if (numValue < 1 || numValue > 31) {
        return; // 유효하지 않은 값이면 상태 업데이트하지 않음
      }
    }

    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleCreateAccount = async () => {
    setShowPinInput(true);
  };

  const handlePinInput = (digit: string) => {
    if (formData.pinNumber.length < 6) {
      setFormData((prev) => ({ ...prev, pinNumber: prev.pinNumber + digit }));
    }
  };

  const handlePinDelete = () => {
    setFormData((prev) => ({
      ...prev,
      pinNumber: prev.pinNumber.slice(0, -1),
    }));
  };

  const handlePinClear = () => {
    setFormData((prev) => ({ ...prev, pinNumber: "" }));
  };

  const handlePinSubmit = async () => {
    if (!formData.pinNumber || formData.pinNumber.length !== 6) {
      toast({
        title: "비밀번호 오류",
        description: "6자리 비밀번호를 입력해주세요.",
        variant: "destructive",
      });
      return;
    }

    setIsCreatingAccount(true);
    setIsLoading(true);
    setIsPinDialogOpen(false);
    setTransferStatus("모임 생성 중...");

    try {
      const response = await authApi.post("/api/v1/gathering", {
        gatheringName: formData.gatheringTitle,
        gatheringIntroduction: formData.gatheringIntroduction,
        memberCount: parseInt(formData.memberCount),
        basicFee: parseInt(formData.basicFee),
        gatheringAccountPW: formData.pinNumber,
        paybackPercent: parseFloat(formData.paybackPercent),
        depositDate: formData.depositDate,
        gatheringDeposit: Number(formData.gatheringDeposit),
      });

      if (response?.gatheringId) {
        toast({
          title: "모임 생성 성공",
          description: "모임이 성공적으로 생성되었습니다.",
        });
        router.push("/group");
      } else {
        throw new Error("모임 생성에 실패했습니다.");
      }
    } catch (error: any) {
      console.error("모임 생성 실패:", error);
      toast({
        title: "모임 생성 실패",
        description: error.message || "모임 생성 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    } finally {
      setIsCreatingAccount(false);
      setIsLoading(false);
    }
  };

  const handleNext = () => {
    switch (step) {
      case 1:
        if (
          !formData.gatheringTitle ||
          !formData.gatheringIntroduction ||
          !formData.memberCount
        ) {
          toast({
            title: "필수 입력",
            description: "모임 이름, 소개, 참여 인원을 모두 입력해주세요.",
            variant: "destructive",
          });
          return;
        }
        break;
      case 2:
        if (!formData.basicFee || !formData.paybackPercent) {
          toast({
            title: "필수 입력",
            description: "기본 회비와 페이백 퍼센트를 입력해주세요.",
            variant: "destructive",
          });
          return;
        }
        // 페이백 퍼센트 유효성 검사
        const paybackPercent = parseFloat(formData.paybackPercent);
        if (
          isNaN(paybackPercent) ||
          paybackPercent < 0 ||
          paybackPercent > 100
        ) {
          toast({
            title: "페이백 퍼센트 오류",
            description: "페이백 퍼센트는 0에서 100 사이의 숫자여야 합니다.",
            variant: "destructive",
          });
          return;
        }
        break;
      case 3:
        if (!formData.accountNumber) {
          toast({
            title: "계좌 개설 필요",
            description: "계좌를 먼저 개설해주세요.",
            variant: "destructive",
          });
          return;
        }
        break;
    }

    if (step < 3) {
      setStep((prev) => prev + 1);
    }
  };

  const handlePrevious = () => {
    if (step > 1) {
      setStep((prev) => prev - 1);
    }
  };

  // 페이백 퍼센트 입력 핸들러 추가
  const handlePaybackInput = (num: string) => {
    const currentValue = formData.paybackPercent;
    const newValue = currentValue + num;

    if (newValue.length <= 3 && parseInt(newValue) <= 100) {
      setFormData((prev) => ({
        ...prev,
        paybackPercent: newValue,
      }));
    }
  };

  const handlePaybackDelete = () => {
    setFormData((prev) => ({
      ...prev,
      paybackPercent: prev.paybackPercent.slice(0, -1),
    }));
  };

  const handlePaybackClear = () => {
    setFormData((prev) => ({
      ...prev,
      paybackPercent: "",
    }));
  };

  const renderStep = () => {
    switch (step) {
      case 1:
        return (
          <>
            <div>
              <Label htmlFor="gatheringTitle">모임 이름</Label>
              <Input
                id="gatheringTitle"
                name="gatheringTitle"
                value={formData.gatheringTitle}
                onChange={handleInputChange}
                placeholder="모임 이름을 입력하세요"
                required
              />
            </div>
            <div>
              <Label htmlFor="gatheringIntroduction">모임 소개</Label>
              <Textarea
                id="gatheringIntroduction"
                name="gatheringIntroduction"
                value={formData.gatheringIntroduction}
                onChange={handleInputChange}
                placeholder="모임에 대한 간단한 소개를 입력하세요"
                required
              />
            </div>
            <div>
              <Label htmlFor="memberCount">참여 인원</Label>
              <Input
                id="memberCount"
                name="memberCount"
                type="number"
                value={formData.memberCount}
                onChange={handleInputChange}
                placeholder="참여 인원 수를 입력하세요"
                required
              />
            </div>
            <div>
              <Label htmlFor="gatheringDeposit">보증금</Label>
              <Input
                id="gatheringDeposit"
                name="gatheringDeposit"
                type="number"
                value={formData.gatheringDeposit}
                onChange={handleInputChange}
                placeholder="보증금을 입력하세요"
                required
              />
            </div>
            <div>
              <Label htmlFor="depositDate">매월 입금일</Label>
              <Input
                id="depositDate"
                name="depositDate"
                type="number"
                min="1"
                max="31"
                value={formData.depositDate}
                onChange={handleInputChange}
                placeholder="매월 입금일을 입력하세요 (1-31)"
                required
              />
            </div>
          </>
        );
      case 2:
        return (
          <>
            <div>
              <Label htmlFor="basicFee">기본 회비</Label>
              <Input
                id="basicFee"
                name="basicFee"
                type="number"
                value={formData.basicFee}
                onChange={handleInputChange}
                placeholder="기본 회비를 입력하세요"
                required
              />
            </div>
            <div>
              <Label htmlFor="paybackPercent">페이백 퍼센트</Label>
              <Input
                id="paybackPercent"
                name="paybackPercent"
                type="number"
                min="0"
                max="100"
                step="1"
                value={formData.paybackPercent}
                onChange={handleInputChange}
                onKeyDown={(e) => {
                  // 숫자 키와 특수 키(Backspace, Delete, Arrow keys)만 허용
                  if (
                    !/[\d\b]/.test(e.key) &&
                    ![
                      "Backspace",
                      "Delete",
                      "ArrowLeft",
                      "ArrowRight",
                      "Tab",
                    ].includes(e.key)
                  ) {
                    e.preventDefault();
                  }
                }}
                placeholder="페이백 퍼센트를 입력하세요 (0-100)"
                required
              />
              <p className="text-sm text-gray-500">
                0에서 100 사이의 숫자를 입력해주세요
              </p>
            </div>
          </>
        );
      case 3:
        return (
          <>
            <div>
              <Label htmlFor="bankName">은행명</Label>
              <Input
                id="bankName"
                name="bankName"
                value="싸피 은행"
                disabled
                className="bg-gray-100"
              />
            </div>
            <div className="flex flex-col gap-4">
              {!showPinInput ? (
                <Button
                  type="button"
                  onClick={handleCreateAccount}
                  disabled={isCreatingAccount || !!formData.accountNumber}
                  className="w-full"
                >
                  {isCreatingAccount ? "계좌 개설 중..." : "계좌 개설하기"}
                </Button>
              ) : (
                <div className="space-y-4">
                  <div className="text-center space-y-2">
                    <h3 className="font-semibold">모임 계좌 비밀번호 설정</h3>
                    <p className="text-sm text-gray-500">
                      모임 계좌에서 사용할 6자리 비밀번호를 입력해주세요.
                    </p>
                  </div>
                  <div>
                    <Label htmlFor="pinNumber"></Label>
                    <Input
                      id="pinNumber"
                      name="pinNumber"
                      type="password"
                      value={formData.pinNumber}
                      onChange={handleInputChange}
                      maxLength={6}
                      placeholder="비밀번호 입력"
                      required
                    />
                  </div>
                  <Button
                    type="button"
                    onClick={handlePinSubmit}
                    disabled={formData.pinNumber.length !== 6}
                    className="w-full bg-blue-500 hover:bg-blue-600 text-white"
                  >
                    {isCreatingAccount ? "처리 중..." : "모임 만들기"}
                  </Button>
                </div>
              )}
              {formData.accountNumber && (
                <div className="p-4 bg-green-50 rounded-lg">
                  <p className="text-sm text-green-800">
                    계좌번호: {formData.accountNumber}
                  </p>
                </div>
              )}
            </div>
          </>
        );
      default:
        return null;
    }
  };

  return (
    <>
      <Header title="새 모임 만들기" showBackButton />
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
            <div className="space-y-4">
              {renderStep()}
              <div className="flex justify-between mt-6">
                {step > 1 && (
                  <Button
                    type="button"
                    onClick={handlePrevious}
                    disabled={isLoading}
                  >
                    이전
                  </Button>
                )}
                {step < 3 && (
                  <Button
                    type="button"
                    onClick={handleNext}
                    className="ml-auto"
                    disabled={isLoading}
                  >
                    다음
                    <ArrowRight className="h-4 w-4 ml-2" />
                  </Button>
                )}
              </div>
            </div>
          </CardContent>
        </Card>
      </main>
    </>
  );
}
