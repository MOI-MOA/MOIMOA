"use client";

import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { Header } from "@/components/Header";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
  CardFooter,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
import { toast } from "@/components/ui/use-toast";
import { 
  AlertCircle, 
  Save, 
  XCircle, 
  Calendar, 
  Users, 
  CreditCard, 
  RefreshCw, 
  ToggleLeft, 
  ToggleRight,
  ArrowLeft,
  Coins
} from "lucide-react";
import { authApi } from "@/lib/api";

export default function EditAutoTransferPage() {
  const router = useRouter();
  const searchParams = useSearchParams();

  // 초기 상태 설정
  const [formData, setFormData] = useState({
    id: Number(searchParams.get("id")),
    groupName: searchParams.get("groupName") || "",
    amount: searchParams.get("amount") || "",
    day: searchParams.get("day") || "",
    account: searchParams.get("account") || "",
    status: searchParams.get("status") === "true",
  });

  const [isLoading, setIsLoading] = useState(false);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleStatusChange = (checked: boolean) => {
    setTimeout(() => {
      setFormData((prev) => ({
        ...prev,
        status: checked,
      }));
    }, 0);
  };

  const saveData = async () => {
    // 숫자 유효성 검사
    const day = Number(formData.day);
    if (isNaN(day) || day < 1 || day > 28) {
      toast({
        title: "입력 오류",
        description: "매월 결제일은 1일부터 28일까지 입력 가능합니다.",
        variant: "destructive",
      });
      return;
    }

    setIsLoading(true);
    try {
      const response = await authApi.patch(
        `/api/v1/profile/autopayment/${formData.id}`,
        {
          amount: Number(formData.amount),
          day: day,
          status: formData.status,
        }
      );

      if (response) {
        toast({
          title: "저장 완료",
          description: "자동이체 정보가 성공적으로 업데이트되었습니다.",
        });
        router.push("/profile/auto-transfer");
      }
    } catch (error) {
      console.error("자동이체 정보 업데이트 실패:", error);
      toast({
        title: "오류 발생",
        description: "자동이체 정보 저장 중 문제가 발생했습니다.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    saveData();
  };

  return (
    <>
      <Header title="자동이체 수정" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-5 pb-16 bg-slate-50">
        <h2 className="text-xl font-semibold text-slate-800 flex items-center">
          <RefreshCw className="h-5 w-5 mr-2 text-blue-600" />
          자동이체 정보 수정
        </h2>
        
        <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
          <CardContent className="p-6">
            <div className="bg-gradient-to-r from-blue-100 to-indigo-50 -mt-6 -mx-6 mb-6 p-6 border-b border-blue-100">
              <h3 className="text-lg font-medium text-blue-800 mb-1">{formData.groupName}</h3>
              <p className="text-sm text-blue-600">
                자동이체 ID: {formData.id}
              </p>
            </div>
            
            <form onSubmit={handleSubmit} className="space-y-5">
              <div className="space-y-2">
                <Label htmlFor="groupName" className="text-slate-700 font-medium flex items-center">
                  <Users className="h-4 w-4 mr-2 text-slate-500" />
                  모임 이름
                </Label>
                <div className="relative">
                  <Input
                    id="groupName"
                    name="groupName"
                    value={formData.groupName}
                    readOnly
                    className="bg-slate-50 border-slate-200 rounded-lg pl-10"
                  />
                  <div className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400">
                    <Users className="h-5 w-5" />
                  </div>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="amount" className="text-slate-700 font-medium flex items-center">
                  <Coins className="h-4 w-4 mr-2 text-slate-500" />
                  금액
                </Label>
                <div className="relative">
                  <Input
                    id="amount"
                    name="amount"
                    type="number"
                    value={formData.amount}
                    readOnly
                    className="bg-slate-50 border-slate-200 rounded-lg pl-10"
                  />
                  <div className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400">
                    <Coins className="h-5 w-5" />
                  </div>
                  <div className="absolute right-3 top-1/2 transform -translate-y-1/2 text-slate-500 bg-slate-100 rounded-md px-2 py-1 text-sm">
                    {formData.amount
                      ? Number(formData.amount).toLocaleString()
                      : "0"}원
                  </div>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="day" className="text-slate-700 font-medium flex items-center">
                  <Calendar className="h-4 w-4 mr-2 text-slate-500" />
                  매월 결제일
                </Label>
                <div className="relative">
                  <Input
                    id="day"
                    name="day"
                    type="number"
                    min="1"
                    max="28"
                    value={formData.day}
                    onChange={handleInputChange}
                    required
                    className="border-slate-200 rounded-lg pl-10 focus:border-blue-400 focus:ring-blue-400"
                  />
                  <div className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400">
                    <Calendar className="h-5 w-5" />
                  </div>
                  <div className="absolute right-3 top-1/2 transform -translate-y-1/2 text-slate-500">
                    일
                  </div>
                </div>
                <p className="text-sm text-slate-500 flex items-center">
                  <AlertCircle className="h-4 w-4 mr-1 text-blue-500" />
                  1일부터 28일까지 선택 가능합니다.
                </p>
              </div>

              <div className="space-y-2">
                <Label htmlFor="account" className="text-slate-700 font-medium flex items-center">
                  <CreditCard className="h-4 w-4 mr-2 text-slate-500" />
                  계좌 정보
                </Label>
                <div className="relative">
                  <Input
                    id="account"
                    name="account"
                    value={formData.account}
                    readOnly
                    className="bg-slate-50 border-slate-200 rounded-lg pl-10"
                  />
                  <div className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400">
                    <CreditCard className="h-5 w-5" />
                  </div>
                </div>
              </div>

              <div className="bg-slate-50 p-4 rounded-xl flex items-center justify-between mt-3">
                <Label htmlFor="status" className="text-slate-700 font-medium flex items-center cursor-pointer">
                  {formData.status ? (
                    <ToggleRight className="h-5 w-5 mr-2 text-blue-600" />
                  ) : (
                    <ToggleLeft className="h-5 w-5 mr-2 text-slate-400" />
                  )}
                  자동이체 {formData.status ? "활성화" : "비활성화"}
                </Label>
                <Switch
                  id="status"
                  checked={formData.status}
                  onCheckedChange={handleStatusChange}
                  className="data-[state=checked]:bg-blue-600"
                />
              </div>
              
              <div className="flex space-x-3 pt-3">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => router.push("/profile/auto-transfer")}
                  disabled={isLoading}
                  className="flex-1 rounded-xl border-slate-200 hover:bg-slate-100 text-slate-700"
                >
                  <ArrowLeft className="h-4 w-4 mr-2" />
                  돌아가기
                </Button>
                <Button 
                  type="submit" 
                  disabled={isLoading} 
                  className="flex-1 rounded-xl bg-blue-600 hover:bg-blue-700"
                >
                  {isLoading ? (
                    <div className="flex items-center justify-center">
                      <RefreshCw className="h-4 w-4 mr-2 animate-spin" />
                      저장 중...
                    </div>
                  ) : (
                    <div className="flex items-center justify-center">
                      <Save className="h-4 w-4 mr-2" />
                      저장하기
                    </div>
                  )}
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>

        {!formData.status && (
          <div className="bg-yellow-50 border-0 shadow-sm rounded-xl p-5 flex items-start">
            <div className="bg-yellow-100 p-2 rounded-full mr-3">
              <AlertCircle className="h-5 w-5 text-yellow-600" />
            </div>
            <div>
              <h3 className="text-sm font-medium text-yellow-800">
                자동이체 비활성화됨
              </h3>
              <p className="text-sm text-yellow-700 mt-1">
                자동이체가 비활성화된 상태입니다. 활성화하려면 위의 스위치를 켜세요.
              </p>
            </div>
          </div>
        )}
      </main>
    </>
  );
}
