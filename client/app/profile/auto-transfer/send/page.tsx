"use client";

import type React from "react";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
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
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import axios from "axios";
import { authApi, publicApi } from "@/lib/api";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";

interface AccountCheckResponse {
  toAccountNo: string;
  amount: number;
  isAccount: boolean;
}

interface TransferResponse {
  transactionId: string;
  status: string;
  // 필요한 다른 필드들 추가
}

export default function SendMoneyPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [amount, setAmount] = useState("");
  const [accountNumber, setAccountNumber] = useState("");
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [pinCode, setPinCode] = useState("");
  const [isConfirmDialogOpen, setIsConfirmDialogOpen] = useState(false);
  const [isPinDialogOpen, setIsPinDialogOpen] = useState(false);
  const [accountName, setAccountName] = useState("");
  const [isAccountCheckDialogOpen, setIsAccountCheckDialogOpen] =
    useState(false);
  const [showPinInput, setShowPinInput] = useState(false);
  const [accountOwner, setAccountOwner] = useState("");
  const [transferStatus, setTransferStatus] = useState<string>("");
  const [stompClient, setStompClient] = useState<any>(null);
  const [toAccountType, setToAccountType] = useState<string>("PERSONAL");
  const [userId, setUserId] = useState<string>("");

  useEffect(() => {
    const account = searchParams.get("account");
    const cost = searchParams.get("cost");
    const type = searchParams.get("type");
    const userIdParam = searchParams.get("userId");

    console.log("Received URL parameters:", {
      account,
      cost,
      type,
      userId: userIdParam,
      allParams: Object.fromEntries(searchParams.entries()),
    });

    if (account) {
      setAccountNumber(account);
    }
    if (cost) {
      setAmount(cost);
    }
    if (type) {
      setToAccountType(type);
      console.log("Setting toAccountType to:", type);
    }
    if (userIdParam) {
      setUserId(userIdParam);
      console.log("Setting userId to:", userIdParam);
    }
  }, [searchParams]);

  useEffect(() => {
    if (!userId) return;

    const socket = new SockJS("http://localhost:8080/ws");
    const client = Stomp.over(socket);
    const accessToken = localStorage.getItem("accessToken");

    client.connect({ Authorization: `Bearer ${accessToken}` }, () => {
      console.log("WebSocke    t Connected");
      // 송금 결과 구독
      const subscriptionPath = `/queue/transfer-results/${userId}`;
      console.log("Subscribing to:", subscriptionPath);

      client.subscribe(subscriptionPath, (message) => {
        const result = message.body;
        console.log("Received WebSocket message:", result);

        if (result === "true") {
          toast({
            title: "송금 완료",
            description: `${accountNumber}로 ${Number(
              amount
            ).toLocaleString()}원이 성공적으로 송금되었습니다.`,
          });
          router.back();
        } else if (result === "송금중 오류가 발생") {
          toast({
            title: "송금 실패",
            description: "송금 중 오류가 발생했습니다. 다시 시도해주세요.",
            variant: "destructive",
          });
        }
        setIsLoading(false);
      });
    });

    setStompClient(client);

    return () => {
      if (client) {
        client.disconnect();
      }
    };
  }, [userId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await authApi.get<AccountCheckResponse>(
        `/api/v1/personal-account/${accountNumber}/${amount}/check`
      );

      console.log("API Response:", response);

      if (response.isAccount) {
        setAccountName(response.toAccountNo);
        setIsConfirmDialogOpen(true);
      } else {
        toast({
          title: "계좌 확인 실패",
          description: "존재하지 않는 계좌번호입니다.",
          variant: "destructive",
        });
      }
    } catch (error: any) {
      console.error("API Error:", error);
      if (error.response?.status === 404) {
        toast({
          title: "계좌 확인 실패",
          description: "존재하지 않는 계좌번호입니다. 다시 확인해주세요.",
          variant: "destructive",
        });
      } else {
        toast({
          title: "오류 발생",
          description: "계좌 확인 중 오류가 발생했습니다. 다시 시도해주세요.",
          variant: "destructive",
        });
      }
    }
  };

  const handleAccountCheckConfirm = () => {
    setIsAccountCheckDialogOpen(false);
    setIsConfirmDialogOpen(true);
  };

  const handleConfirm = () => {
    setIsConfirmDialogOpen(false);
    setIsPinDialogOpen(true);
  };

  const handlePinSubmit = async () => {
    setIsLoading(true);
    setIsPinDialogOpen(false);
    setTransferStatus("송금 처리 중...");

    console.log("Submitting transfer with toAccountType:", toAccountType);

    try {
      const response: TransferResponse = await authApi.post(
        `/api/v1/personal-account/transfer`,
        {
          fromAccountType: "PERSONAL",
          toAccountType: toAccountType,
          toAccountNo: accountNumber,
          tradeDetail: message || "",
          transferAmount: Number(amount),
          accountPw: pinCode,
        }
      );

      // WebSocket을 통해 결과를 받을 때까지 대기
      // 결과는 WebSocket 구독 핸들러에서 처리됨
    } catch (error: any) {
      if (error.response?.status === 400) {
        toast({
          title: "PIN 오류",
          description: "올바른 PIN 번호를 입력해주세요.",
          variant: "destructive",
        });
      } else {
        toast({
          title: "송금 실패",
          description: "송금 중 오류가 발생했습니다. 다시 시도해주세요.",
          variant: "destructive",
        });
      }
      setIsLoading(false);
    }
  };

  const handlePinInput = (digit: string) => {
    if (pinCode.length < 6) {
      setPinCode((prev) => prev + digit);
    }
  };

  const handlePinDelete = () => {
    setPinCode((prev) => prev.slice(0, -1));
  };

  const handlePinClear = () => {
    setPinCode("");
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
                <div className="space-y-2">
                  <Label htmlFor="message">메시지</Label>
                  <Input
                    id="message"
                    placeholder="상대방에게 보여질 메시지를 입력하세요"
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    maxLength={50}
                  />
                </div>
              </div>
              <Button
                type="submit"
                className="w-full mt-4"
                disabled={isLoading}
              >
                {isLoading ? transferStatus : "송금하기"}
              </Button>
            </form>
          </CardContent>
        </Card>
      </main>

      <Dialog
        open={isAccountCheckDialogOpen}
        onOpenChange={setIsAccountCheckDialogOpen}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>계좌 확인</DialogTitle>
            <DialogDescription>아래 계좌로 송금하시겠습니까?</DialogDescription>
          </DialogHeader>
          <div className="py-4 space-y-2">
            <p className="text-lg font-semibold text-center">
              {accountName}에게 보냅니다
            </p>
            <p className="text-sm text-center text-gray-500">
              계좌번호: <span className="font-semibold">{accountNumber}</span>
            </p>
            {message && (
              <p className="text-sm text-center text-gray-500 mt-2">
                메시지: <span className="font-semibold">{message}</span>
              </p>
            )}
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setIsAccountCheckDialogOpen(false)}
            >
              취소
            </Button>
            <Button onClick={handleAccountCheckConfirm}>확인</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={isConfirmDialogOpen} onOpenChange={setIsConfirmDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>송금 정보 확인</DialogTitle>
            <DialogDescription>
              아래 정보가 맞는지 확인해주세요.
            </DialogDescription>
          </DialogHeader>
          <div className="py-4 space-y-2">
            <p className="text-sm">
              계좌번호: <span className="font-semibold">{accountNumber}</span>
            </p>
            <p className="text-lg font-semibold text-center">
              {Number(amount).toLocaleString()}원
            </p>
            {message && (
              <p className="text-sm text-center text-gray-500">
                메시지: <span className="font-semibold">{message}</span>
              </p>
            )}
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setIsConfirmDialogOpen(false)}
            >
              취소
            </Button>
            <Button onClick={handleConfirm}>확인</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={isPinDialogOpen} onOpenChange={setIsPinDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>PIN 번호 입력</DialogTitle>
            <DialogDescription>
              송금을 완료하려면 6자리 PIN 번호를 입력해주세요.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <Input
              type="password"
              placeholder="PIN 번호"
              value={pinCode}
              readOnly
              className="text-center text-2xl tracking-widest"
            />
            <div className="grid grid-cols-3 gap-2">
              {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((num) => (
                <Button
                  key={num}
                  onClick={() => handlePinInput(num.toString())}
                  className="text-2xl py-6"
                >
                  {num}
                </Button>
              ))}
              <Button onClick={handlePinClear} className="text-lg py-6">
                Clear
              </Button>
              <Button
                onClick={() => handlePinInput("0")}
                className="text-2xl py-6"
              >
                0
              </Button>
              <Button onClick={handlePinDelete} className="text-lg py-6">
                Delete
              </Button>
            </div>
            <Button
              onClick={handlePinSubmit}
              className="w-full"
              disabled={pinCode.length !== 6}
            >
              확인
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
}
