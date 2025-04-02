"use client";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { Header } from "@/components/Header";
import { authApi, publicApi } from "@/lib/api";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Switch } from "@/components/ui/switch";
import {
  Calendar,
  AlertCircle,
  CheckCircle2,
  SendHorizontal,
  AlertTriangle,
} from "lucide-react";
import { toast } from "@/components/ui/use-toast";

interface AutoTransfer {
  id: number; // Long
  amount: number; // int
  day: number; // int
  status: boolean; // boolean
  account: string; // String
  deposit: number; // Long
  groupName: string; // String
  myBalance: number; // int
}

interface AutoTransferResponse {
  userId: number;
  accountBalance: number; // Long
  autoTransfers: AutoTransfer[];
}

export default function AutoTransferPage() {
  const router = useRouter();
  const searchParams = useSearchParams();

  // 사용자의 계좌 잔액
  const [accountBalance, setAccountBalance] = useState<number>(0);

  // 자동이체 데이터
  const [autoTransfers, setAutoTransfers] = useState<
    Array<{
      id: number;
      groupName: string;
      amount: number;
      day: number;
      nextDate: string;
      status: string;
      account: string;
      deposit: number;
      myBalance: number;
    }>
  >([]);

  // 로딩 상태
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;

    const fetchData = async () => {
      try {
        setIsLoading(true);
        const response = await authApi.get<
          AutoTransferResponse,
          AutoTransferResponse
        >("/api/v1/profile/autopayment");
        console.log("API 응답:", response);

        if (!isMounted) return;

        // API 응답 데이터를 상태에 설정
        if (response) {
          const { accountBalance, autoTransfers } = response;
          if (
            typeof accountBalance === "number" &&
            Array.isArray(autoTransfers)
          ) {
            setAccountBalance(accountBalance);
            setAutoTransfers(
              autoTransfers.map((transfer: AutoTransfer) => ({
                ...transfer,
                status: transfer.status ? "active" : "inactive",
                nextDate: new Date().toISOString().split("T")[0],
              }))
            );
          } else {
            console.error("데이터 타입이 올바르지 않습니다:", {
              accountBalance: typeof accountBalance,
              autoTransfers: Array.isArray(autoTransfers),
            });
            throw new Error("데이터 타입이 올바르지 않습니다.");
          }
        } else {
          console.error("응답이 없습니다.");
          throw new Error("응답 데이터가 올바르지 않습니다.");
        }
      } catch (error) {
        if (!isMounted) return;
        toast({
          title: "오류",
          description: "데이터를 불러오는데 실패했습니다.",
          variant: "destructive",
        });
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    };

    fetchData();

    return () => {
      isMounted = false;
    };
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
  const handleEdit = (transfer: {
    id: number;
    groupName: string;
    amount: number;
    day: number;
    account: string;
    status: string;
  }) => {
    console.log("수정하려는 자동이체 데이터:", transfer);

    if (!transfer) {
      console.log("transfer 객체가 null입니다.");
      toast({
        title: "오류",
        description: "수정할 수 없는 자동이체입니다.",
        variant: "destructive",
      });
      return;
    }

    if (!transfer.id) console.log("id가 없습니다.");
    if (!transfer.groupName) console.log("groupName이 없습니다.");
    if (!transfer.amount) console.log("amount가 없습니다.");
    if (!transfer.day) console.log("day가 없습니다.");
    if (!transfer.account) console.log("account가 없습니다.");
    if (!transfer.status) console.log("status가 없습니다.");

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
    cost: number,
    deposit: number,
    myBalance: number
  ) => {
    const params = new URLSearchParams({
      account: account,
      cost: cost.toString(),
      userId: "1", // TODO: 실제 사용자 ID로 변경 필요
      accountBalance: accountBalance.toString(),
      autoTransfers: JSON.stringify([
        {
          id: id,
          amount: cost,
          day: 1, // TODO: 실제 날짜로 변경 필요
          status: true,
          account: account,
          deposit: deposit,
          groupName: groupName,
          myBalance: myBalance,
        },
      ]),
    });
    router.push(`/profile/auto-transfer/send?${params.toString()}`);
  };

  return (
    <>
      <Header title="" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16">
        {isLoading ? (
          <div className="flex items-center justify-center h-64">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-purple-500"></div>
          </div>
        ) : (
          <>
            <Card>
              <CardHeader>
                <CardTitle>자동이체 요약</CardTitle>
                <CardDescription>
                  현재 설정된 자동이체 현황입니다
                </CardDescription>
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
                        {accountBalance ? accountBalance.toLocaleString() : "0"}
                        원
                      </p>
                    </div>
                    <Button
                      onClick={() => router.push("/profile/auto-transfer/send")}
                      className="bg-purple-600 hover:bg-purple-700 text-white"
                    >
                      <SendHorizontal className="h-4 w-4 mr-2" />
                      송금하기
                    </Button>
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
                            transfer.status === "active"
                              ? "outline"
                              : "secondary"
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
                        {(transfer.amount ?? 0).toLocaleString()}원
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
                              {transfer.myBalance?.toLocaleString() ?? "0"}원
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
                              transfer.amount,
                              transfer.deposit,
                              transfer.myBalance
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
          </>
        )}
      </main>
    </>
  );
}
