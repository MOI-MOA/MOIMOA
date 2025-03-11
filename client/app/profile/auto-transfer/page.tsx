"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Header } from "@/components/Header"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Switch } from "@/components/ui/switch"
import { Calendar, AlertCircle, CheckCircle2, SendHorizontal, AlertTriangle } from "lucide-react"
import { toast } from "@/components/ui/use-toast"

export default function AutoTransferPage() {
  const router = useRouter()

  // 사용자의 계좌 잔액 (실제로는 API에서 가져와야 함)
  const [accountBalance, setAccountBalance] = useState(500000)

  // 자동이체 데이터 (실제로는 API에서 가져와야 함)
  const [autoTransfers, setAutoTransfers] = useState([
    {
      id: 1,
      groupName: "회사 동료",
      amount: 30000,
      day: 15,
      nextDate: "2024-04-15",
      status: "active",
      account: "신한은행 110-123-456789",
      deposit: 100000, // 보증금
      myBalance: 150000, // 나의 잔액
    },
    {
      id: 2,
      groupName: "대학 친구들",
      amount: 20000,
      day: 20,
      nextDate: "2024-04-20",
      status: "active",
      account: "국민은행 123-45-6789012",
      deposit: 50000,
      myBalance: 100000,
    },
    {
      id: 3,
      groupName: "동호회",
      amount: 15000,
      day: 5,
      nextDate: "2024-04-05",
      status: "inactive",
      account: "우리은행 1002-123-456789",
      deposit: 80000,
      myBalance: 120000,
    },
    {
      id: 4,
      groupName: "독서 모임",
      amount: 10000,
      day: 10,
      nextDate: "2024-04-10",
      status: "active",
      account: "카카오뱅크 3333-12-3456789",
      deposit: 50000,
      myBalance: 30000, // 보증금보다 낮은 잔액
    },
  ])

  // 활성 자동이체 개수 계산
  const activeTransfers = autoTransfers.filter((transfer) => transfer.status === "active")

  // 월 총액 계산
  const monthlyTotal = activeTransfers.reduce((sum, transfer) => sum + transfer.amount, 0)

  // 자동이체 상태 토글 함수
  const toggleTransferStatus = (id) => {
    setAutoTransfers((prevTransfers) =>
      prevTransfers.map((transfer) => {
        if (transfer.id === id) {
          const newStatus = transfer.status === "active" ? "inactive" : "active"
          toast({
            title: `자동이체 ${newStatus === "active" ? "활성화" : "비활성화"}`,
            description: `${transfer.groupName} 자동이체가 ${newStatus === "active" ? "활성화" : "비활성화"} 되었습니다.`,
          })
          return { ...transfer, status: newStatus }
        }
        return transfer
      }),
    )
  }

  // 자동이체 수정 페이지로 이동
  const handleEdit = (id) => {
    router.push(`/profile/auto-transfer/edit/${id}`)
  }

  // 송금 페이지로 이동하는 함수
  const handleTransfer = (id: number, groupName: string, account: string) => {
    router.push(
      `/profile/auto-transfer/send?groupName=${encodeURIComponent(groupName)}&account=${encodeURIComponent(account)}`,
    )
  }

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
                <p className="text-xl font-bold text-blue-600">{activeTransfers.length}건</p>
              </div>
              <div className="p-4 bg-green-50 rounded-lg">
                <p className="text-sm text-gray-500">월 총액</p>
                <p className="text-xl font-bold text-green-600">{monthlyTotal.toLocaleString()}원</p>
              </div>
              <div className="p-4 bg-purple-50 rounded-lg col-span-2 flex justify-between items-center">
                <div>
                  <p className="text-sm text-gray-500">내 계좌 잔액</p>
                  <p className="text-xl font-bold text-purple-600">{accountBalance.toLocaleString()}원</p>
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
              <span className="text-sm font-medium">이번 달 자동이체 후 예상 잔액</span>
              <span className="text-lg font-bold text-green-600">
                {(accountBalance - monthlyTotal).toLocaleString()}원
              </span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2.5">
              <div
                className="bg-green-600 h-2.5 rounded-full"
                style={{ width: `${((accountBalance - monthlyTotal) / accountBalance) * 100}%` }}
              ></div>
            </div>
            <p className="text-xs text-gray-500 mt-1">
              {monthlyTotal > accountBalance
                ? `잔액이 ${(monthlyTotal - accountBalance).toLocaleString()}원 부족합니다.`
                : `자동이체 후 ${(accountBalance - monthlyTotal).toLocaleString()}원이 남습니다.`}
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
                      <p className="text-sm text-gray-500">{transfer.account}</p>
                    </div>
                    <Badge
                      variant={transfer.status === "active" ? "outline" : "secondary"}
                      className={
                        transfer.status === "active" ? "text-green-600 bg-green-50" : "text-gray-500 bg-gray-100"
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
                  <div className="text-lg font-semibold mb-2">{transfer.amount.toLocaleString()}원</div>
                  <div className="grid grid-cols-2 gap-2 text-sm">
                    <div>
                      <p className="text-gray-500">보증금</p>
                      <p className="font-medium">{transfer.deposit.toLocaleString()}원</p>
                    </div>
                    <div>
                      <p className="text-gray-500">나의 잔액</p>
                      <div className="flex items-center">
                        <p className={`font-medium ${transfer.myBalance < transfer.deposit ? "text-red-500" : ""}`}>
                          {transfer.myBalance.toLocaleString()}원
                        </p>
                        {transfer.myBalance < transfer.deposit && (
                          <AlertTriangle className="h-4 w-4 ml-1 text-red-500" />
                        )}
                      </div>
                      {transfer.myBalance < transfer.deposit && (
                        <p className="text-xs text-red-500 mt-1">
                          보증금보다 {(transfer.deposit - transfer.myBalance).toLocaleString()}원 부족
                        </p>
                      )}
                    </div>
                  </div>
                </div>
                <div className="border-t p-3 bg-gray-50 flex justify-between items-center">
                  <div className="flex items-center">
                    <span className="text-sm mr-2">자동이체 활성화</span>
                    <Switch
                      checked={transfer.status === "active"}
                      onCheckedChange={() => toggleTransferStatus(transfer.id)}
                    />
                  </div>
                  <div className="flex space-x-2">
                    <Button variant="outline" size="sm" onClick={() => handleEdit(transfer.id)}>
                      수정
                    </Button>
                    <Button
                      variant="secondary"
                      size="sm"
                      onClick={() => handleTransfer(transfer.id, transfer.groupName, transfer.account)}
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
  )
}

