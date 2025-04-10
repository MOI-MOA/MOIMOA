"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { 
  ArrowRight, 
  ArrowLeft, 
  Users, 
  MessageSquare, 
  Calculator, 
  Percent, 
  Building, 
  CreditCard, 
  Lock, 
  Check, 
  RefreshCw,
  Calendar,
  Coins,
  Wallet,
  AlertCircle,
  XCircle,
  Trash2
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Card, CardContent } from "@/components/ui/card";
import { toast } from "@/components/ui/use-toast";
import { Header } from "@/components/Header";
import { authApi } from "@/lib/api";

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

    // 수치 입력 필드에 대한 검증 추가
    if (["memberCount", "gatheringDeposit", "depositDate", "basicFee", "paybackPercent"].includes(name)) {
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
      
      // depositDate 특별 처리 (1-31 사이 값만)
      if (name === "depositDate" && (numValue < 1 || numValue > 31)) {
        return;
      }
      
      // paybackPercent 특별 처리 (0-100 사이 값만)
      if (name === "paybackPercent" && numValue > 100) {
        return;
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
          !formData.memberCount ||
          !formData.gatheringDeposit ||
          !formData.depositDate
        ) {
          toast({
            title: "필수 입력",
            description: "모든 필드를 입력해주세요.",
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

  const getStepTitle = () => {
    switch (step) {
      case 1:
        return "모임 기본 정보";
      case 2:
        return "모임 회비 정보";
      case 3:
        return "모임 계좌 설정";
      default:
        return "";
    }
  };

  const getStepIcon = () => {
    switch (step) {
      case 1:
        return <Users className="h-5 w-5 mr-2 text-blue-600" />;
      case 2:
        return <Calculator className="h-5 w-5 mr-2 text-blue-600" />;
      case 3:
        return <CreditCard className="h-5 w-5 mr-2 text-blue-600" />;
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
              <Label htmlFor="gatheringTitle" className="text-slate-700 font-medium flex items-center">
                <Users className="h-4 w-4 mr-1.5 text-slate-500" />
                모임 이름
              </Label>
              <div className="relative">
                <Input
                  id="gatheringTitle"
                  name="gatheringTitle"
                  value={formData.gatheringTitle}
                  onChange={handleInputChange}
                  placeholder="모임 이름을 입력하세요"
                  required
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <Users className="h-5 w-5 text-slate-400" />
                </div>
              </div>
            </div>
            
            <div className="space-y-2">
              <Label htmlFor="gatheringIntroduction" className="text-slate-700 font-medium flex items-center">
                <MessageSquare className="h-4 w-4 mr-1.5 text-slate-500" />
                모임 소개
              </Label>
              <div className="relative">
                <Textarea
                  id="gatheringIntroduction"
                  name="gatheringIntroduction"
                  value={formData.gatheringIntroduction}
                  onChange={handleInputChange}
                  placeholder="모임에 대한 간단한 소개를 입력하세요"
                  required
                  className="pl-10 pt-10 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400 min-h-[120px]"
                />
                <div className="absolute top-3 left-3 pointer-events-none">
                  <MessageSquare className="h-5 w-5 text-slate-400" />
                </div>
              </div>
            </div>
            
            <div className="space-y-2">
              <Label htmlFor="memberCount" className="text-slate-700 font-medium flex items-center">
                <Users className="h-4 w-4 mr-1.5 text-slate-500" />
                참여 인원
              </Label>
              <div className="relative">
                <Input
                  id="memberCount"
                  name="memberCount"
                  type="number"
                  min="1"
                  value={formData.memberCount}
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
            
            <div className="space-y-2">
              <Label htmlFor="gatheringDeposit" className="text-slate-700 font-medium flex items-center">
                <Coins className="h-4 w-4 mr-1.5 text-slate-500" />
                보증금
              </Label>
              <div className="relative">
                <Input
                  id="gatheringDeposit"
                  name="gatheringDeposit"
                  type="number"
                  min="0"
                  value={formData.gatheringDeposit}
                  onChange={handleInputChange}
                  onWheel={(e) => e.currentTarget.blur()}
                  placeholder="보증금을 입력하세요"
                  required
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <Coins className="h-5 w-5 text-slate-400" />
                </div>
                <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                  <span className="text-slate-400">원</span>
                </div>
              </div>
            </div>
            
            <div className="space-y-2">
              <Label htmlFor="depositDate" className="text-slate-700 font-medium flex items-center">
                <Calendar className="h-4 w-4 mr-1.5 text-slate-500" />
                매월 입금일
              </Label>
              <div className="relative">
                <Input
                  id="depositDate"
                  name="depositDate"
                  type="number"
                  min="1"
                  max="31"
                  value={formData.depositDate}
                  onChange={handleInputChange}
                  onWheel={(e) => e.currentTarget.blur()}
                  placeholder="매월 입금일을 입력하세요 (1-31)"
                  required
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <Calendar className="h-5 w-5 text-slate-400" />
                </div>
                <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                  <span className="text-slate-400">일</span>
                </div>
              </div>
              <p className="text-sm text-slate-500 flex items-center pl-1">
                <AlertCircle className="h-3.5 w-3.5 mr-1 text-blue-500" />
                매월 이 날짜에 회비를 납부해야 합니다.
              </p>
            </div>
          </>
        );
      case 2:
        return (
          <>
            <div className="space-y-2">
              <Label htmlFor="basicFee" className="text-slate-700 font-medium flex items-center">
                <Wallet className="h-4 w-4 mr-1.5 text-slate-500" />
                기본 회비
              </Label>
              <div className="relative">
                <Input
                  id="basicFee"
                  name="basicFee"
                  type="number"
                  min="0"
                  value={formData.basicFee}
                  onChange={handleInputChange}
                  onWheel={(e) => e.currentTarget.blur()}
                  placeholder="기본 회비를 입력하세요"
                  required
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <Wallet className="h-5 w-5 text-slate-400" />
                </div>
                <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                  <span className="text-slate-400">원</span>
                </div>
              </div>
              <p className="text-sm text-slate-500 flex items-center pl-1">
                <AlertCircle className="h-3.5 w-3.5 mr-1 text-blue-500" />
                모임 회비로 사용될 금액입니다.
              </p>
            </div>
            
            <div className="space-y-2">
              <Label htmlFor="paybackPercent" className="text-slate-700 font-medium flex items-center">
                <Percent className="h-4 w-4 mr-1.5 text-slate-500" />
                페이백 퍼센트
              </Label>
              <div className="relative">
                <Input
                  id="paybackPercent"
                  name="paybackPercent"
                  type="number"
                  min="0"
                  max="100"
                  step="1"
                  value={formData.paybackPercent}
                  onChange={handleInputChange}
                  onWheel={(e) => e.currentTarget.blur()}
                  onKeyDown={(e) => {
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
                  className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                />
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <Percent className="h-5 w-5 text-slate-400" />
                </div>
                <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                  <span className="text-slate-400">%</span>
                </div>
              </div>
              <p className="text-sm text-slate-500 flex items-center pl-1">
                <AlertCircle className="h-3.5 w-3.5 mr-1 text-blue-500" />
                참여 시 페이백 받을 퍼센트를 설정합니다. (0-100%)
              </p>
            </div>
          </>
        );
      case 3:
        return (
          <>
            <div className="space-y-2">
              <Label htmlFor="bankName" className="text-slate-700 font-medium flex items-center">
                <Building className="h-4 w-4 mr-1.5 text-slate-500" />
                은행명
              </Label>
              <div className="relative">
                <Input
                  id="bankName"
                  name="bankName"
                  value="싸피 은행"
                  disabled
                  className="pl-10 py-6 rounded-xl bg-slate-50 border-slate-200"
                />
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <Building className="h-5 w-5 text-slate-400" />
                </div>
              </div>
            </div>
            
            <div className="flex flex-col gap-4 mt-3">
              {!showPinInput ? (
                <Button
                  type="button"
                  onClick={handleCreateAccount}
                  disabled={isCreatingAccount || !!formData.accountNumber}
                  className="w-full py-6 rounded-xl bg-blue-600 hover:bg-blue-700 text-white"
                >
                  {isCreatingAccount ? (
                    <div className="flex items-center">
                      <RefreshCw className="h-4 w-4 mr-2 animate-spin" />
                      계좌 개설 중...
                    </div>
                  ) : (
                    <div className="flex items-center">
                      <CreditCard className="h-5 w-5 mr-2" />
                      계좌 개설하기
                    </div>
                  )}
                </Button>
              ) : (
                <div className="space-y-5 bg-blue-50 p-5 rounded-xl border border-blue-100">
                  <div className="text-center space-y-2">
                    <div className="bg-blue-100 w-12 h-12 rounded-full flex items-center justify-center mx-auto mb-3">
                      <Lock className="h-6 w-6 text-blue-600" />
                    </div>
                    <h3 className="font-semibold text-blue-800">모임 계좌 비밀번호 설정</h3>
                    <p className="text-sm text-blue-700">
                      모임 계좌에서 사용할 6자리 비밀번호를 입력해주세요.
                    </p>
                  </div>
                  
                  {/* PIN 입력 UI */}
                  <div>
                    <div className="flex justify-center mb-4">
                      <div className="flex items-center gap-2">
                        {Array.from({ length: 6 }).map((_, i) => (
                          <div
                            key={i}
                            className={`w-10 h-10 border-2 rounded-lg flex items-center justify-center ${
                              i < formData.pinNumber.length
                                ? "bg-blue-100 border-blue-300"
                                : "border-slate-200"
                            }`}
                          >
                            {i < formData.pinNumber.length ? "•" : ""}
                          </div>
                        ))}
                      </div>
                    </div>
                    
                    <div className="grid grid-cols-3 gap-3">
                      {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((num) => (
                        <Button
                          key={num}
                          type="button"
                          onClick={() => handlePinInput(num.toString())}
                          className="text-xl font-medium py-6 rounded-xl hover:bg-blue-50 border border-slate-200 bg-white text-slate-800"
                          variant="outline"
                        >
                          {num}
                        </Button>
                      ))}
                      <Button 
                        type="button"
                        onClick={handlePinClear} 
                        className="text-sm py-6 rounded-xl hover:bg-red-50 border border-slate-200 bg-white text-red-600"
                        variant="outline"
                      >
                        <Trash2 className="h-5 w-5" />
                      </Button>
                      <Button
                        type="button"
                        onClick={() => handlePinInput("0")}
                        className="text-xl font-medium py-6 rounded-xl hover:bg-blue-50 border border-slate-200 bg-white text-slate-800"
                        variant="outline"
                      >
                        0
                      </Button>
                      <Button 
                        type="button"
                        onClick={handlePinDelete} 
                        className="text-sm py-6 rounded-xl hover:bg-slate-100 border border-slate-200 bg-white text-slate-700"
                        variant="outline"
                      >
                        <XCircle className="h-5 w-5" />
                      </Button>
                    </div>
                  </div>
                  
                  <Button
                    type="button"
                    onClick={handlePinSubmit}
                    disabled={formData.pinNumber.length !== 6 || isCreatingAccount}
                    className="w-full py-6 rounded-xl bg-blue-600 hover:bg-blue-700 text-white"
                  >
                    {isCreatingAccount ? (
                      <div className="flex items-center justify-center">
                        <RefreshCw className="h-4 w-4 mr-2 animate-spin" />
                        모임 생성 중...
                      </div>
                    ) : (
                      <div className="flex items-center justify-center">
                        <Check className="h-5 w-5 mr-2" />
                        모임 만들기
                      </div>
                    )}
                  </Button>
                </div>
              )}
              
              {formData.accountNumber && (
                <div className="p-5 bg-green-50 rounded-xl border border-green-200">
                  <div className="flex items-start">
                    <div className="bg-green-100 p-2 rounded-full mr-3">
                      <Check className="h-5 w-5 text-green-600" />
                    </div>
                    <div>
                      <h3 className="text-sm font-medium text-green-800 mb-1">계좌 개설 완료</h3>
                      <p className="text-sm text-green-700">
                        계좌번호: <span className="font-semibold">{formData.accountNumber}</span>
                      </p>
                    </div>
                  </div>
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
              
              <div className="space-y-5">
                {renderStep()}
                
                <div className="flex space-x-3 pt-3">
                  {step > 1 && (
                    <Button
                      type="button"
                      onClick={handlePrevious}
                      disabled={isLoading}
                      className="flex-1 rounded-xl border-slate-200 hover:bg-slate-100 text-slate-700"
                      variant="outline"
                    >
                      <ArrowLeft className="h-4 w-4 mr-2" />
                      이전
                    </Button>
                  )}
                  
                  {step < 3 && (
                    <Button
                      type="button"
                      onClick={handleNext}
                      className={`${step > 1 ? 'flex-1' : 'w-full'} rounded-xl bg-blue-600 hover:bg-blue-700`}
                      disabled={isLoading}
                    >
                      <div className="flex items-center justify-center">
                        다음
                        <ArrowRight className="h-4 w-4 ml-2" />
                      </div>
                    </Button>
                  )}
                </div>
              </div>
            </CardContent>
          </Card>
          
          <div className="bg-blue-50 p-4 rounded-xl border border-blue-100 mt-4">
            <div className="flex">
              <div className="bg-blue-100 p-2 rounded-full mr-3 flex-shrink-0">
                <AlertCircle className="h-5 w-5 text-blue-600" />
              </div>
              <div>
                <h3 className="text-sm font-medium text-blue-800 mb-1">모임 생성 안내</h3>
                <p className="text-xs text-blue-700">
                  모임을 생성하면 자동으로 모임장이 됩니다. 모임 정보는 생성 후에도 수정할 수 있습니다.
                  계좌 비밀번호는 분실 시 복구할 수 없으니 안전하게 보관해주세요.
                </p>
              </div>
            </div>
          </div>
        </div>
      </main>
    </>
  );
}