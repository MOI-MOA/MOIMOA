"use client";

import type React from "react";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { Header } from "@/components/Header";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "@/components/ui/use-toast";
import { publicApi, authApi } from "@/lib/api";

interface AccountCheckResponse {
  httpStatus: number;
  datas: {
    accountOwner: string;
  };
  message: string;
}

export default function SendMoneyInputPage() {
  const router = useRouter();
  const [amount, setAmount] = useState("");
  const [accountNumber, setAccountNumber] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      const response = await authApi.get<
        AccountCheckResponse,
        AccountCheckResponse
      >(`/api/v1/personal-account/check/${accountNumber}/${amount}`);

      if (response.httpStatus === 200) {
        router.push(
          `/profile/auto-transfer/send?account=${accountNumber}&cost=${amount}`
        );
      } else {
        toast({
          title: "계좌 확인 실패",
          description: response.message || "계좌번호를 다시 확인해주세요.",
          variant: "destructive",
        });
      }
    } catch (error) {
      console.error("계좌 확인 중 오류 발생:", error);
      toast({
        title: "오류 발생",
        description: "계좌 확인 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <Header title="송금하기" showBackButton />
      <main className="flex-1 overflow-auto p-4 pb-16">
        <Card>
          <CardHeader>
            <CardTitle>송금하기</CardTitle>
            <CardDescription>
              송금할 계좌와 금액을 입력해주세요.
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <form onSubmit={handleSubmit}>
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="accountNumber">계좌번호</Label>
                  <Input
                    id="accountNumber"
                    placeholder="계좌번호를 입력하세요"
                    value={accountNumber}
                    onChange={(e) => setAccountNumber(e.target.value)}
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="amount">송금 금액</Label>
                  <Input
                    id="amount"
                    type="number"
                    placeholder="0"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    required
                  />
                </div>
              </div>
              <Button
                type="submit"
                className="w-full mt-4"
                disabled={isLoading}
              >
                {isLoading ? "확인 중..." : "다음"}
              </Button>
            </form>
          </CardContent>
        </Card>
      </main>
    </>
  );
}
