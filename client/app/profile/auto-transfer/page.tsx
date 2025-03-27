"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Header } from "@/components/Header";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Switch } from "@/components/ui/switch";
import {
  Calendar,
  AlertCircle,
  CheckCircle2,
  SendHorizontal,
  AlertTriangle,
} from "lucide-react";
import { toast } from "@/components/ui/use-toast";
import { authApi, publicApi } from "@/lib/api";

interface AutoTransfer {
  id: number;
  groupName: string;
  amount: number;
  day: number;
  nextDate: string;
  status: string;
  account: string;
  deposit: number;
  myBalance: number;
}

interface AccountCheckResponse {
  isValid: boolean;
}

export default function AutoTransferPage() {
  const router = useRouter();
  const [accountBalance, setAccountBalance] = useState(0);
  const [autoTransfers, setAutoTransfers] = useState<AutoTransfer[]>([]);
  const [accountNumber, setAccountNumber] = useState("");

  // 자동이체 목록 가져오기
  useEffect(() => {
    const fetchAutoTransfers = async () => {
      try {
        const response = await authApi.get<AutoTransfer[], AutoTransfer[]>(
          "/api/v1/auto-transfer/list"
        );
        setAutoTransfers(response);
      } catch (error) {
        console.error("자동이체 목록 조회 실패:", error);
        toast({
          title: "오류",
          description: "자동이체 목록을 불러오는데 실패했습니다.",
          variant: "destructive",
        });
      }
    };

    fetchAutoTransfers();
  }, []);

  // 활성 자동이체 개수 계산
  const activeTransfers = autoTransfers.filter(
    (transfer) => transfer.status === "active"
  );

  // 월 총액 계산
  const monthlyTotal = activeTransfers.reduce(
    (sum, transfer) => sum + transfer.amount,
    0
  );

  // 자동이체 수정 페이지로 이동
  const handleEdit = (transfer: AutoTransfer) => {
    const params = new URLSearchParams({
      id: transfer.id.toString(),
      groupName: transfer.groupName,
      amount: transfer.amount.toString(),
      day: transfer.day.toString(),
      account: transfer.account,
      status: transfer.status,
    });
    router.push(`/profile/auto-transfer/edit?${params.toString()}`);
  };

  // 송금 페이지로 이동하는 함수
  const handleTransfer = (
    id: number,
    groupName: string,
    account: string,
    cost: number
  ) => {
    router.push(
      `/profile/auto-transfer/send?groupName=${encodeURIComponent(
        groupName
      )}&account=${encodeURIComponent(account)}&cost=${encodeURIComponent(
        cost.toString()
      )}&accountId=${id}`
    );
  };

  // 송금하기 버튼 클릭 핸들러
  const handleSendMoney = async () => {
    if (!accountNumber) {
      toast({
        title: "입력 오류",
        description: "계좌번호를 입력해주세요.",
        variant: "destructive",
      });
      return;
    }

    try {
      // 계좌 확인 API 호출
      const response = await publicApi.get<
        AccountCheckResponse,
        AccountCheckResponse
      >(`/api/v1/personal-account/check?accountNo=${accountNumber}`);

      if (response.isValid) {
        router.push("/profile/auto-transfer/send");
      } else {
        toast({
          title: "계좌 확인 실패",
          description: "존재하지 않는 계좌번호입니다.",
          variant: "destructive",
        });
      }
    } catch (error) {
      console.error("계좌 확인 중 오류 발생:", error);
      toast({
        title: "오류",
        description: "계좌 확인 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    }
  };

  return (
    <>
      <Header title="자동이체 현황" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16">
        <Card>
          <CardHeader>
            <CardTitle>자동이체 요약</CardTitle>
            <CardDescription>현재 설정된 자동이체 현황입니다</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 gap-4">
              <div className="p-4 bg-blue-50 rounded-lg">
                <p className="text-sm text-gray-500">활성 자동이체</p>
                <p className="text-xl font-bold text-blue-600">
                  {activeTransfers.length}건
                </p>
              </div>
              <div className="p-4 bg-green-50 rounded-lg">
                <p className="text-sm text-gray-500">월 총액</p>
                <p className="text-xl font-bold text-green-600">
                  {monthlyTotal.toLocaleString()}원
                </p>
              </div>
              <div className="p-4 bg-purple-50 rounded-lg col-span-2 flex justify-between items-center">
                <div>
                  <p className="text-sm text-gray-500">내 계좌 잔액</p>
                  <p className="text-xl font-bold text-purple-600">
                    {accountBalance.toLocaleString()}원
                  </p>
                </div>
                <div className="flex gap-2">
                  <Input
                    placeholder="계좌번호 입력"
                    value={accountNumber}
                    onChange={(e) => setAccountNumber(e.target.value)}
                    className="w-48"
                  />
                  <Button
                    onClick={handleSendMoney}
                    className="bg-purple-600 hover:bg-purple-700 text-white"
                  >
                    <SendHorizontal className="h-4 w-4 mr-2" />
                    송금하기
                  </Button>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* 잔액 비교 */}
        <Card>
          <CardContent className="p-4">
            <div className="flex justify-between items-center mb-2">
              <span className="text-sm font-medium">
                이번 달 자동이체 후 예상 잔액
              </span>
              <span className="text-lg font-bold text-green-600">
                {(accountBalance - monthlyTotal).toLocaleString()}원
              </span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2.5">
              <div
                className="bg-green-600 h-2.5 rounded-full"
                style={{
                  width: `${
                    ((accountBalance - monthlyTotal) / accountBalance) * 100
                  }%`,
                }}
              ></div>
            </div>
            <p className="text-xs text-gray-500 mt-1">
              {monthlyTotal > accountBalance
                ? `잔액이 ${(
                    monthlyTotal - accountBalance
                  ).toLocaleString()}원 부족합니다.`
                : `자동이체 후 ${(
                    accountBalance - monthlyTotal
                  ).toLocaleString()}원이 남습니다.`}
            </p>
          </CardContent>
        </Card>

        {/* 기존 코드 ... */}

        <div className="space-y-4">
          <h2 className="text-lg font-semibold mb-4">자동이체 목록</h2>

          {autoTransfers.map((transfer) => (
            <Card key={transfer.id} className="overflow-hidden">
              <CardContent className="p-0">
                <div className="p-4">
                  <div className="flex justify-between items-start mb-2">
                    <div>
                      <h3 className="font-medium">{transfer.groupName}</h3>
                      <p className="text-sm text-gray-500">
                        {transfer.account}
                      </p>
                    </div>
                    <Badge
                      variant={
                        transfer.status === "active" ? "outline" : "secondary"
                      }
                      className={
                        transfer.status === "active"
                          ? "text-green-600 bg-green-50"
                          : "text-gray-500 bg-gray-100"
                      }
                    >
                      {transfer.status === "active" ? "활성" : "비활성"}
                    </Badge>
                  </div>
                  <div className="flex items-center text-sm text-gray-600 mb-2">
                    <Calendar className="h-4 w-4 mr-1" />
                    <span>
                      매월 {transfer.day}일 / 다음 결제: {transfer.nextDate}
                    </span>
                  </div>
                  <div className="text-lg font-semibold mb-2">
                    {transfer.amount.toLocaleString()}원
                  </div>
                  <div className="grid grid-cols-2 gap-2 text-sm">
                    <div>
                      <p className="text-gray-500">보증금</p>
                      <p className="font-medium">
                        {transfer.deposit.toLocaleString()}원
                      </p>
                    </div>
                    <div>
                      <p className="text-gray-500">나의 잔액</p>
                      <div className="flex items-center">
                        <p
                          className={`font-medium ${
                            transfer.myBalance < transfer.deposit
                              ? "text-red-500"
                              : ""
                          }`}
                        >
                          {transfer.myBalance.toLocaleString()}원
                        </p>
                        {transfer.myBalance < transfer.deposit && (
                          <AlertTriangle className="h-4 w-4 ml-1 text-red-500" />
                        )}
                      </div>
                      {transfer.myBalance < transfer.deposit && (
                        <p className="text-xs text-red-500 mt-1">
                          보증금보다{" "}
                          {(
                            transfer.deposit - transfer.myBalance
                          ).toLocaleString()}
                          원 부족
                        </p>
                      )}
                    </div>
                  </div>
                </div>
                <div className="border-t p-3 bg-gray-50 flex justify-between items-center">
                  <div className="flex items-center"></div>
                  <div className="flex space-x-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleEdit(transfer)}
                    >
                      수정
                    </Button>
                    <Button
                      variant="secondary"
                      size="sm"
                      onClick={() =>
                        handleTransfer(
                          transfer.id,
                          transfer.groupName,
                          transfer.account,
                          transfer.amount
                        )
                      }
                    >
                      <SendHorizontal className="h-4 w-4 mr-2" />
                      송금
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        <Card>
          <CardHeader>
            <CardTitle>최근 결제 내역</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center justify-between p-3 border rounded-lg">
              <div>
                <div className="font-medium">회사 동료</div>
                <div className="text-sm text-gray-500">2024-03-15</div>
              </div>
              <div className="flex items-center">
                <span className="font-semibold mr-2">30,000원</span>
                <CheckCircle2 className="h-5 w-5 text-green-500" />
              </div>
            </div>
            <div className="flex items-center justify-between p-3 border rounded-lg">
              <div>
                <div className="font-medium">대학 친구들</div>
                <div className="text-sm text-gray-500">2024-03-20</div>
              </div>
              <div className="flex items-center">
                <span className="font-semibold mr-2">20,000원</span>
                <CheckCircle2 className="h-5 w-5 text-green-500" />
              </div>
            </div>
            <div className="flex items-center justify-between p-3 border rounded-lg">
              <div>
                <div className="font-medium">동호회</div>
                <div className="text-sm text-gray-500">2024-03-05</div>
              </div>
              <div className="flex items-center">
                <span className="font-semibold mr-2">15,000원</span>
                <AlertCircle className="h-5 w-5 text-red-500" />
              </div>
            </div>
          </CardContent>
        </Card>
      </main>
    </>
  );
}
