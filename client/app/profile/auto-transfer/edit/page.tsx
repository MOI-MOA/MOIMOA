"use client";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import axios from "axios";
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
import { AlertCircle } from "lucide-react";
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
    status: searchParams.get("status") === "active",
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
    setFormData((prev) => ({
      ...prev,
      status: checked,
    }));
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

  const handleSaveClick = () => {
    saveData();
  };

  return (
    <>
      <Header title="자동이체 수정" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16">
        <Card>
          <CardHeader>
            <CardTitle>자동이체 정보 수정</CardTitle>
            <CardDescription>자동이체 정보를 수정하세요</CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="groupName">모임 이름</Label>
                <Input
                  id="groupName"
                  name="groupName"
                  value={formData.groupName}
                  readOnly
                  className="bg-gray-100"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="amount">금액</Label>
                <div className="flex items-center space-x-2">
                  <Input
                    id="amount"
                    name="amount"
                    type="number"
                    value={formData.amount}
                    readOnly
                    className="bg-gray-100"
                  />
                  <div className="text-gray-500">원</div>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="day">매월 결제일</Label>
                <div className="flex items-center space-x-2">
                  <Input
                    id="day"
                    name="day"
                    type="number"
                    min="1"
                    max="28"
                    value={formData.day}
                    onChange={handleInputChange}
                    required
                  />
                  <div className="text-gray-500">일</div>
                </div>
                <p className="text-sm text-gray-500">
                  1일부터 28일까지 선택 가능합니다.
                </p>
              </div>

              <div className="space-y-2">
                <Label htmlFor="account">계좌 정보</Label>
                <Input
                  id="account"
                  name="account"
                  value={formData.account}
                  readOnly
                  className="bg-gray-100"
                />
              </div>

              <div className="flex items-center justify-between pt-2">
                <Label htmlFor="status">상태</Label>
                <Switch
                  id="status"
                  checked={formData.status}
                  onCheckedChange={(checked) =>
                    setFormData((prev) => ({ ...prev, status: checked }))
                  }
                />
              </div>
            </form>
          </CardContent>
          <CardFooter className="flex justify-between">
            <Button
              variant="outline"
              onClick={() => router.push("/profile/auto-transfer")}
              disabled={isLoading}
            >
              취소
            </Button>
            <Button onClick={handleSaveClick} disabled={isLoading}>
              {isLoading ? "저장 중..." : "저장하기"}
            </Button>
          </CardFooter>
        </Card>

        {formData.status === "inactive" && (
          <div className="bg-yellow-50 border border-yellow-200 rounded-md p-4 flex items-start">
            <AlertCircle className="h-5 w-5 text-yellow-500 mt-0.5 mr-2 flex-shrink-0" />
            <div>
              <h3 className="text-sm font-medium text-yellow-800">
                자동이체 비활성화됨
              </h3>
              <p className="text-sm text-yellow-700 mt-1">
                자동이체가 비활성화된 상태입니다. 활성화하려면 위의 스위치를
                켜세요.
              </p>
            </div>
          </div>
        )}
      </main>
    </>
  );
}
