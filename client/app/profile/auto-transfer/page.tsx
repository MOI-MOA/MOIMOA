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
  CreditCard,
  Wallet,
  ChevronRight,
  Clock,
  Edit,
  Banknote,
} from "lucide-react";
import { toast } from "@/components/ui/use-toast";

interface AutoTransfer {
  id: number;
  amount: number;
  day: number;
  status: boolean;
  account: string;
  deposit: number;
  groupName: string;
  myBalance: number;
  paymentStatus: boolean;
}

interface AutoTransferResponse {
  userId: number;
  accountBalance: number;
  autoTransfers: AutoTransfer[];
}

export default function AutoTransferPage() {
  const router = useRouter();
  const searchParams = useSearchParams();

  // 사용자의 계좌 잔액
  const [accountBalance, setAccountBalance] = useState<number>(0);
  const [userId, setUserId] = useState<number>(0);

  // 자동이체 데이터
  const [autoTransfers, setAutoTransfers] = useState<
    Array<{
      id: number;
      groupName: string;
      amount: number;
      day: number;
      nextDate: string;
      status: boolean;
      account: string;
      deposit: number;
      myBalance: number;
      paymentStatus: boolean;
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
          const { accountBalance, autoTransfers, userId } = response;
          if (
            typeof accountBalance === "number" &&
            Array.isArray(autoTransfers)
          ) {
            setAccountBalance(accountBalance);
            setUserId(userId);
            setAutoTransfers(
              autoTransfers.map((transfer: AutoTransfer) => ({
                ...transfer,
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

  // 활성 자동이체 개수 계산 수정 - status가 true인 자동이체만 필터링
  const activeTransfers = autoTransfers.filter(transfer => transfer.status === true).length;

  // 월 총액 계산 수정 - 활성화된 자동이체의 amount 합계
  const monthlyTotal = autoTransfers
    .filter(transfer => transfer.status === true)
    .reduce((sum, transfer) => sum + transfer.amount, 0);

  // 자동이체 수정 페이지로 이동
  const handleEdit = (transfer: {
    id: number;
    groupName: string;
    amount: number;
    day: number;
    account: string;
    status: boolean;
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

    const params = new URLSearchParams({
      id: transfer.id.toString(),
      groupName: transfer.groupName,
      amount: transfer.amount.toString(),
      day: transfer.day.toString(),
      account: transfer.account,
      status: transfer.status.toString(),
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
      userId: userId.toString(),
      accountBalance: accountBalance.toString(),
      type: "GATHERING",
      autoTransfers: JSON.stringify([
        {
          id: id,
          amount: cost,
          day: 1,
          status: true,
          account: account,
          deposit: deposit,
          groupName: groupName,
          myBalance: myBalance,
          type: "GATHERING",
        },
      ]),
    });
    router.push(`/profile/auto-transfer/send?${params.toString()}`);
  };

  return (
    <>
      <Header title="자동이체 현황" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16 bg-slate-50">
        {isLoading ? (
          <div className="flex items-center justify-center h-64">
            <div className="animate-spin rounded-full h-10 w-10 border-4 border-blue-500 border-t-transparent"></div>
          </div>
        ) : (
          <>
            <div className="space-y-3">
              <h2 className="text-xl font-semibold text-slate-800 flex items-center">
                <CreditCard className="h-5 w-5 mr-2 text-blue-600" />
                자동이체 요약
              </h2>
              
              <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
                <CardContent className="p-6">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="p-4 bg-blue-50 rounded-xl flex items-center">
                      <div className="bg-blue-100 p-2 rounded-full mr-4">
                        <Calendar className="h-6 w-6 text-blue-600" />
                      </div>
                      <div>
                        <p className="text-sm text-slate-600 mb-1">활성 자동이체</p>
                        <p className="text-xl font-bold text-blue-700">
                          {activeTransfers}건
                        </p>
                      </div>
                    </div>
                    
                    <div className="p-4 bg-teal-50 rounded-xl flex items-center">
                      <div className="bg-teal-100 p-2 rounded-full mr-4">
                        <Banknote className="h-6 w-6 text-teal-600" />
                      </div>
                      <div>
                        <p className="text-sm text-slate-600 mb-1">월 총액</p>
                        <p className="text-xl font-bold text-teal-700">
                          {monthlyTotal.toLocaleString()}원
                        </p>
                      </div>
                    </div>
                    
                    <div className="p-5 bg-indigo-50 rounded-xl col-span-1 md:col-span-2 flex flex-col md:flex-row justify-between md:items-center gap-4">
                      <div className="flex items-center">
                        <div className="bg-indigo-100 p-2 rounded-full mr-4">
                          <Wallet className="h-6 w-6 text-indigo-600" />
                        </div>
                        <div>
                          <p className="text-sm text-slate-600 mb-1">내 계좌 잔액</p>
                          <p className="text-xl font-bold text-indigo-700">
                            {accountBalance ? accountBalance.toLocaleString() : "0"}원
                          </p>
                        </div>
                      </div>
                      <Button
                        onClick={() =>
                          router.push(
                            `/profile/auto-transfer/send?type=PERSONAL&userId=${userId}`
                          )
                        }
                        className="bg-indigo-600 hover:bg-indigo-700 text-white rounded-full px-5 py-2"
                      >
                        <SendHorizontal className="h-4 w-4 mr-2" />
                        송금하기
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* 잔액 비교 */}
            <div className="space-y-3">
              <h2 className="text-xl font-semibold text-slate-800 flex items-center">
                <Wallet className="h-5 w-5 mr-2 text-blue-600" />
                예상 잔액
              </h2>
              
              <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
                <CardContent className="p-6">
                  <div className="flex justify-between items-center mb-3">
                    <span className="font-medium text-slate-700">
                      이번 달 자동이체 후 예상 잔액
                    </span>
                    <span className="text-lg font-bold text-teal-600">
                      {(accountBalance - monthlyTotal).toLocaleString()}원
                    </span>
                  </div>
                  
                  <div className="w-full bg-slate-200 rounded-full h-2">
                    <div
                      className="bg-teal-500 h-2 rounded-full transition-all duration-500"
                      style={{
                        width: `${Math.max(
                          Math.min(
                            ((accountBalance - monthlyTotal) / accountBalance) * 100,
                            100
                          ),
                          0
                        )}%`,
                      }}
                    ></div>
                  </div>
                  
                  <div className="mt-3 flex items-center">
                    {monthlyTotal > accountBalance ? (
                      <div className="flex items-center text-red-600 text-sm bg-red-50 px-3 py-1.5 rounded-full">
                        <AlertTriangle className="h-4 w-4 mr-1.5" />
                        잔액이 {(monthlyTotal - accountBalance).toLocaleString()}원 부족합니다
                      </div>
                    ) : (
                      <div className="flex items-center text-teal-600 text-sm bg-teal-50 px-3 py-1.5 rounded-full">
                        <CheckCircle2 className="h-4 w-4 mr-1.5" />
                        자동이체 후 {(accountBalance - monthlyTotal).toLocaleString()}원이 남습니다
                      </div>
                    )}
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* 자동이체 목록 */}
            <div className="space-y-3">
              <h2 className="text-xl font-semibold text-slate-800 flex items-center">
                <CreditCard className="h-5 w-5 mr-2 text-blue-600" />
                자동이체 목록
              </h2>

              {autoTransfers.length > 0 ? (
                <div className="space-y-3">
                  {autoTransfers.map((transfer, index) => (
                    <Card 
                      key={transfer.id} 
                      className="border-0 shadow-sm hover:shadow-md rounded-xl overflow-hidden transition-all duration-200"
                    >
                      <CardContent className="p-0">
                        <div className={`border-l-4 ${
                          transfer.status 
                            ? transfer.myBalance < transfer.deposit
                              ? 'border-amber-500'
                              : 'border-teal-500'
                            : 'border-slate-300'
                        } p-5`}>
                          <div className="flex justify-between items-start mb-3">
                            <div>
                              <h3 className="font-medium text-lg text-slate-800">{transfer.groupName}</h3>
                              <p className="text-sm text-slate-500 mt-0.5">
                                {transfer.account}
                              </p>
                            </div>
                            <Badge
                              variant={transfer.status ? "outline" : "secondary"}
                              className={
                                transfer.status
                                  ? "text-teal-600 bg-teal-50 border-teal-200"
                                  : "text-slate-500 bg-slate-100"
                              }
                            >
                              {transfer.status ? "활성" : "비활성"}
                            </Badge>
                          </div>
                          
                          <div className="flex items-center text-sm text-slate-600 mb-3 bg-slate-50 px-3 py-2 rounded-lg">
                            <Calendar className="h-4 w-4 mr-2 text-slate-500" />
                            <span>
                              매월 {transfer.day}일 · 다음 결제:{" "}
                              <span className="font-medium">
                                {transfer.paymentStatus
                                  ? new Date(
                                      new Date().getFullYear(),
                                      new Date().getMonth(),
                                      transfer.day
                                    ).toLocaleDateString()
                                  : new Date(
                                      new Date().getFullYear(),
                                      new Date().getMonth() + 1,
                                      transfer.day
                                    ).toLocaleDateString()}
                              </span>
                            </span>
                          </div>
                          
                          <div className="text-lg font-bold text-indigo-700 mb-3">
                            {(transfer.amount ?? 0).toLocaleString()}원
                          </div>
                          
                          <div className="grid grid-cols-2 gap-4 mb-4">
                            <div className="bg-slate-50 p-3 rounded-lg">
                              <p className="text-sm text-slate-500 mb-1">보증금</p>
                              <p className="font-medium text-slate-800">
                                {transfer.deposit.toLocaleString()}원
                              </p>
                            </div>
                            
                            <div className={`p-3 rounded-lg ${
                              transfer.myBalance < transfer.deposit
                                ? 'bg-red-50'
                                : 'bg-slate-50'
                            }`}>
                              <p className="text-sm text-slate-500 mb-1">나의 잔액</p>
                              <div className="flex items-center">
                                <p
                                  className={`font-medium ${
                                    transfer.myBalance < transfer.deposit
                                      ? "text-red-600"
                                      : "text-slate-800"
                                  }`}
                                >
                                  {transfer.myBalance?.toLocaleString() ?? "0"}원
                                </p>
                                {transfer.myBalance < transfer.deposit && (
                                  <AlertTriangle className="h-4 w-4 ml-1.5 text-red-500" />
                                )}
                              </div>
                              {transfer.myBalance < transfer.deposit && (
                                <p className="text-xs text-red-600 mt-1">
                                  보증금보다 {(transfer.deposit - transfer.myBalance).toLocaleString()}원 부족
                                </p>
                              )}
                            </div>
                          </div>
                          
                          <div className="flex justify-end space-x-3">
                            <Button
                              variant="outline"
                              size="sm"
                              className="rounded-lg border-slate-200 hover:bg-slate-100 text-slate-700"
                              onClick={() => handleEdit(transfer)}
                            >
                              <Edit className="h-4 w-4 mr-1.5" />
                              수정
                            </Button>
                            
                            <Button
                              variant="secondary"
                              size="sm"
                              className="rounded-lg bg-indigo-100 hover:bg-indigo-200 text-indigo-700 border-none"
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
                              <SendHorizontal className="h-4 w-4 mr-1.5" />
                              송금
                            </Button>
                          </div>
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              ) : (
                <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
                  <CardContent className="p-8 text-center text-slate-500 bg-white">
                    <div className="flex flex-col items-center">
                      <div className="bg-slate-100 p-4 rounded-full mb-3">
                        <CreditCard className="h-8 w-8 text-slate-400" />
                      </div>
                      <p className="font-medium">등록된 자동이체가 없습니다</p>
                    </div>
                  </CardContent>
                </Card>
              )}
            </div>
          </>
        )}
      </main>
    </>
  );
}
