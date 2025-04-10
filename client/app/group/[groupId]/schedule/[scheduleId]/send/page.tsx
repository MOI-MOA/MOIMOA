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
  CardFooter,
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
import { authApi } from "@/lib/api";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import {
  Send,
  Wallet,
  MessageSquare,
  CreditCard,
  ArrowRight,
  ArrowLeft,
  CheckCircle,
  XCircle,
  Lock,
  Trash2,
  RefreshCcw,
  Info,
  AlertTriangle,
} from "lucide-react";

interface AccountCheckResponse {
  toAccountNo: string;
  amount: number;
  isAccount: boolean;
}

interface TransferResponse {
  transactionId: string;
  status: string;
}

export default function SendMoneyPage({
  params,
}: {
  params: { groupId: string; scheduleId: string }
}) {
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
  const [isAccountCheckDialogOpen, setIsAccountCheckDialogOpen] = useState(false);
  const [showPinInput, setShowPinInput] = useState(false);
  const [accountOwner, setAccountOwner] = useState("");
  const [transferStatus, setTransferStatus] = useState<string>("");
  const [stompClient, setStompClient] = useState<any>(null);
  const [toAccountType, setToAccountType] = useState<string>("PERSONAL");
  const [userId, setUserId] = useState<string>("");

  const { scheduleId } = params;

  useEffect(() => {
    const account = searchParams.get("account");
    const cost = searchParams.get("cost");
    const type = searchParams.get("type");
    const userIdParam = searchParams.get("userId");

    if (account) setAccountNumber(account);
    if (cost) setAmount(cost);
    if (type) setToAccountType(type);
    if (userIdParam) setUserId(userIdParam);
  }, [searchParams]);

  useEffect(() => {
    if (!userId) return;
    // // 상대 경로 사용
    // const socket = new SockJS("/ws", null, {
    //     transports: ['xhr-streaming', 'xhr-polling']
    // });
    const socket = new SockJS("https://j12b110.p.ssafy.io/ws");  // 절대 경로 사용
    const client = Stomp.over(socket);
    const accessToken = localStorage.getItem("accessToken");

    client.connect({ Authorization: `Bearer ${accessToken}` }, () => {
      console.log("WebSocket Connected");
      const subscriptionPath = `/queue/transfer-results/${userId}`;


      client.subscribe(subscriptionPath, (message) => {
        const result = message.body;

        if (result === "true") {
          toast({
            title: "송금 완료",
            description: `${accountNumber}로 ${Number(amount).toLocaleString()}원이 성공적으로 송금되었습니다.`,
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
      if (client) client.disconnect();
    };
  }, [userId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await authApi.get<AccountCheckResponse>(
        `/api/v1/schedule-account/${accountNumber}/${amount}/check`
      );

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

  const handleConfirm = () => {
    setIsConfirmDialogOpen(false);
    setIsPinDialogOpen(true);
  };

  const handlePinSubmit = async () => {
    setIsLoading(true);
    setIsPinDialogOpen(false);
    setTransferStatus("송금 처리 중...");

    try {
      await authApi.post(`/api/v1/schedule-account/transfer`, {
        fromAccountType: "SCHEDULE",
        toAccountType: toAccountType,
        toAccountNo: accountNumber,
        tradeDetail: message || "",
        transferAmount: Number(amount),
        accountPw: pinCode,
        scheduleId: scheduleId,
      });
      // WebSocket을 통해 결과를 받을 때까지 대기
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
      <main className="flex-1 overflow-auto p-4 pb-16 bg-slate-50">
        <div className="space-y-3">
          <h2 className="text-xl font-semibold text-slate-800 flex items-center">
            <Send className="h-5 w-5 mr-2 text-blue-600" />
            송금하기
          </h2>
          
          <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
            <CardContent className="p-6">
              <form onSubmit={handleSubmit} className="space-y-5">
                <div className="space-y-5">
                  <div className="space-y-2">
                    <Label htmlFor="accountNumber" className="text-slate-700 font-medium block">
                      계좌번호
                    </Label>
                    <div className="relative">
                      <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                        <CreditCard className="h-5 w-5 text-slate-400" />
                      </div>
                      <Input
                        id="accountNumber"
                        placeholder="계좌번호를 입력하세요"
                        value={accountNumber}
                        onChange={(e) => setAccountNumber(e.target.value)}
                        required
                        className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                      />
                    </div>
                  </div>
                  
                  <div className="space-y-2">
                    <Label htmlFor="amount" className="text-slate-700 font-medium block">
                      송금 금액
                    </Label>
                    <div className="relative">
                      <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                        <Wallet className="h-5 w-5 text-slate-400" />
                      </div>
                      <Input
                        id="amount"
                        type="number"
                        placeholder="0"
                        value={amount}
                        onChange={(e) => setAmount(e.target.value)}
                        required
                        className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                      />
                      <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                        <span className="text-slate-400">원</span>
                      </div>
                    </div>
                  </div>
                  
                  <div className="space-y-2">
                    <Label htmlFor="message" className="text-slate-700 font-medium block">
                      메시지 (선택사항)
                    </Label>
                    <div className="relative">
                      <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                        <MessageSquare className="h-5 w-5 text-slate-400" />
                      </div>
                      <Input
                        id="message"
                        placeholder="상대방에게 보여질 메시지를 입력하세요"
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        maxLength={50}
                        className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                      />
                    </div>
                  </div>
                </div>
                
                <Button
                  type="submit"
                  className="w-full py-6 rounded-xl bg-blue-600 hover:bg-blue-700 transition-colors"
                  disabled={isLoading || !accountNumber || !amount}
                >
                  {isLoading ? (
                    <div className="flex items-center">
                      <RefreshCcw className="h-4 w-4 mr-2 animate-spin" />
                      {transferStatus}
                    </div>
                  ) : (
                    <div className="flex items-center justify-center">
                      <Send className="h-5 w-5 mr-2" />
                      송금하기
                    </div>
                  )}
                </Button>
              </form>
            </CardContent>
          </Card>
          
          <div className="bg-blue-50 p-4 rounded-xl border border-blue-100 mt-4">
            <div className="flex">
              <div className="bg-blue-100 p-2 rounded-full mr-3 flex-shrink-0">
                <AlertTriangle className="h-5 w-5 text-blue-600" />
              </div>
              <div>
                <h3 className="text-sm font-medium text-blue-800 mb-1">송금 주의사항</h3>
                <p className="text-xs text-blue-700">
                  계좌번호를 정확히 입력해주세요. 송금은 즉시 처리되며 취소가 불가능합니다.
                  송금 완료 후 상대방의 계좌로 즉시 이체됩니다.
                </p>
              </div>
            </div>
          </div>
        </div>
      </main>

      {/* 송금 정보 확인 다이얼로그 */}
      <Dialog open={isConfirmDialogOpen} onOpenChange={setIsConfirmDialogOpen}>
        <DialogContent className="border-0 shadow-md rounded-xl">
          <DialogHeader>
            <DialogTitle className="text-center text-xl">송금 정보 확인</DialogTitle>
            <DialogDescription className="text-center">
              아래 정보가 맞는지 확인해주세요.
            </DialogDescription>
          </DialogHeader>
          <div className="py-4 space-y-4">
            <div className="bg-blue-50 p-5 rounded-xl text-center">
              <p className="text-2xl font-bold text-blue-700">
                {Number(amount).toLocaleString()}원
              </p>
            </div>
            
            <div className="bg-slate-50 p-4 rounded-xl">
              <div className="flex justify-between items-center">
                <span className="text-slate-600">계좌번호:</span>
                <span className="font-medium text-slate-800">{accountNumber}</span>
              </div>
              <div className="flex justify-between items-center mt-2">
                <span className="text-slate-600">수취인:</span>
                <span className="font-medium text-slate-800">{accountName || '확인 중...'}</span>
              </div>
              {message && (
                <div className="flex justify-between items-center mt-2">
                  <span className="text-slate-600">메시지:</span>
                  <span className="font-medium text-slate-800">{message}</span>
                </div>
              )}
            </div>
          </div>
          <DialogFooter className="flex space-x-2">
            <Button
              variant="outline"
              onClick={() => setIsConfirmDialogOpen(false)}
              className="flex-1 rounded-lg border-slate-200"
            >
              <XCircle className="h-4 w-4 mr-2" />
              취소
            </Button>
            <Button 
              onClick={handleConfirm}
              className="flex-1 rounded-lg bg-blue-600 hover:bg-blue-700"
            >
              <ArrowRight className="h-4 w-4 mr-2" />
              다음
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* PIN 입력 다이얼로그 */}
      <Dialog open={isPinDialogOpen} onOpenChange={setIsPinDialogOpen}>
        <DialogContent className="border-0 shadow-md rounded-xl">
          <DialogHeader>
            <DialogTitle className="text-center text-xl">
              <div className="flex items-center justify-center mb-1">
                <Lock className="h-6 w-6 mr-2 text-blue-600" />
                PIN 번호 입력
              </div>
            </DialogTitle>
            <DialogDescription className="text-center">
              송금을 완료하려면 6자리 PIN 번호를 입력해주세요.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-6 py-2">
            <div className="flex justify-center mb-4">
              <div className="flex items-center gap-2">
                {Array.from({ length: 6 }).map((_, i) => (
                  <div
                    key={i}
                    className={`w-10 h-10 border-2 rounded-lg flex items-center justify-center ${
                      i < pinCode.length
                        ? "bg-blue-100 border-blue-300"
                        : "border-slate-200"
                    }`}
                  >
                    {i < pinCode.length ? "•" : ""}
                  </div>
                ))}
              </div>
            </div>
            
            <div className="grid grid-cols-3 gap-3">
              {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((num) => (
                <Button
                  key={num}
                  onClick={() => handlePinInput(num.toString())}
                  className="text-xl font-medium py-6 rounded-xl hover:bg-blue-50 border border-slate-200 bg-white text-slate-800"
                  variant="outline"
                >
                  {num}
                </Button>
              ))}
              <Button 
                onClick={handlePinClear} 
                className="text-sm py-6 rounded-xl hover:bg-red-50 border border-slate-200 bg-white text-red-600"
                variant="outline"
              >
                <Trash2 className="h-5 w-5" />
              </Button>
              <Button
                onClick={() => handlePinInput("0")}
                className="text-xl font-medium py-6 rounded-xl hover:bg-blue-50 border border-slate-200 bg-white text-slate-800"
                variant="outline"
              >
                0
              </Button>
              <Button 
                onClick={handlePinDelete} 
                className="text-sm py-6 rounded-xl hover:bg-slate-100 border border-slate-200 bg-white text-slate-700"
                variant="outline"
              >
                <XCircle className="h-5 w-5" />
              </Button>
            </div>
            
            <Button
              onClick={handlePinSubmit}
              className="w-full py-6 rounded-xl bg-blue-600 hover:bg-blue-700"
              disabled={pinCode.length !== 6}
            >
              <div className="flex items-center justify-center">
                <Send className="h-5 w-5 mr-2" />
                송금 완료
              </div>
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
}
