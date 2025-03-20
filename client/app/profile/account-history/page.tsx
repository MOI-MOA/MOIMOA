"use client"

import React, { use, useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { format } from "date-fns"
import { ko } from "date-fns/locale"
import { CalendarIcon, Download, Search } from "lucide-react"
import { Header } from "@/components/Header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Calendar } from "@/components/ui/calendar"
import axios from "axios"
import { toast } from "@/components/ui/use-toast"

// 기본 데이터 타입 정의
interface Transaction {
  tradeDetail: string
  tradeTime: string
  trade_amount: number
  trade_balance: number
  trade_partener_name: string
}

interface AccountData {
  gathering_name: string
  gathering_account_no: number
  gathering_account_balance: number
  totaldeposit: number
  totalWithdrawal: number
  tradeList: Transaction[]
}

// 기본 데이터
const DEFAULT_ACCOUNT_DATA: AccountData = {
  gathering_name: "배한진",
  gathering_account_no: 12345678901234,
  gathering_account_balance: 500000,
  totaldeposit: 1200000,
  totalWithdrawal: 700000,
  tradeList: [
    {
      tradeDetail: "송금받음",
      tradeTime: "2025-03-20T10:15:30",
      trade_amount: 300000,
      trade_balance: 500000,
      trade_partener_name: "김철수"
    },
    {
      tradeDetail: "편의점 결제",
      tradeTime: "2025-03-19T18:45:10",
      trade_amount: -5000,
      trade_balance: 200000,
      trade_partener_name: "GS25"
    },
    {
      tradeDetail: "회비 입금",
      tradeTime: "2025-03-18T14:30:00",
      trade_amount: 500000,
      trade_balance: 205000,
      trade_partener_name: "이영희"
    },
    {
      tradeDetail: "음식점 결제",
      tradeTime: "2025-03-17T20:10:25",
      trade_amount: -70000,
      trade_balance: 5000,
      trade_partener_name: "BBQ 치킨"
    }
  ]
}

export default function AccountHistoryPage() {
  const router = useRouter()
  const [searchTerm, setSearchTerm] = useState("")
  const [transactionType, setTransactionType] = useState("all")
  const [accountData, setAccountData] = useState<AccountData>(DEFAULT_ACCOUNT_DATA)
  const [isLoading, setIsLoading] = useState(true)

  // API 데이터 가져오기
  useEffect(() => {
    const fetchAccountData = async () => {
      try {
        const response = await axios.get('/api/v1/account/history')
        setAccountData(response.data)
      } catch (error) {
        console.error('계좌 내역을 불러오는데 실패했습니다:', error)
        toast({
          title: "데이터 로드 실패",
          description: "기본 데이터를 표시합니다.",
          variant: "destructive",
        })
        // 에러 시 기본 데이터 사용
        setAccountData(DEFAULT_ACCOUNT_DATA)
      } finally {
        setIsLoading(false)
      }
    }

    fetchAccountData()
  }, [])

  // 필터링된 거래 내역
  const filteredTransactions = accountData.tradeList.filter((transaction) => {
    // 검색어 필터링
    const matchesSearch =
      searchTerm === "" ||
      transaction.tradeDetail.toLowerCase().includes(searchTerm.toLowerCase()) ||
      transaction.trade_partener_name.toLowerCase().includes(searchTerm.toLowerCase())

    // 거래 유형 필터링
    const matchesType =
      transactionType === "all" ||
      (transactionType === "deposit" && transaction.trade_amount > 0) ||
      (transactionType === "withdrawal" && transaction.trade_amount < 0)

    return matchesSearch && matchesType
  })

  // 날짜/시간 포맷 함수
  const formatDateTime = (dateTimeString: string) => {
    const date = new Date(dateTimeString)
    return format(date, "yyyy년 MM월 dd일 HH:mm", { locale: ko })
  }

  if (isLoading) {
    return <div className="flex items-center justify-center h-screen">로딩 중...</div>
  }

  return (
    <>
      <Header title="내 계좌 내역" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16">
        {/* 계좌 정보 */}
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-lg">{accountData.gathering_name}님의 계좌</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-sm text-gray-500 mb-2">{accountData.gathering_account_no}</p>
            <div className="text-2xl font-bold">{accountData.gathering_account_balance.toLocaleString()}원</div>
            <div className="grid grid-cols-2 gap-4 mt-4">
              <div>
                <p className="text-sm text-gray-500">총 입금액</p>
                <p className="text-lg font-semibold text-green-600">+{accountData.totaldeposit.toLocaleString()}원</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">총 출금액</p>
                <p className="text-lg font-semibold text-red-600">-{accountData.totalWithdrawal.toLocaleString()}원</p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* 필터 섹션 */}
        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold">거래 내역</h2>
            <Button variant="outline" size="sm" onClick={() => {
              setSearchTerm("")
              setTransactionType("all")
            }}>
              필터 초기화
            </Button>
          </div>

          <div className="flex space-x-2">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
              <Input
                className="pl-9"
                placeholder="검색"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>

            <Select value={transactionType} onValueChange={setTransactionType}>
              <SelectTrigger className="w-[120px]">
                <SelectValue placeholder="유형" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">전체</SelectItem>
                <SelectItem value="deposit">입금</SelectItem>
                <SelectItem value="withdrawal">출금</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        {/* 거래 내역 목록 */}
        <div className="space-y-3">
          {filteredTransactions.length > 0 ? (
            filteredTransactions.map((transaction, index) => (
              <Card key={index} className="hover:shadow-sm transition-shadow">
                <CardContent className="p-4">
                  <div className="flex justify-between items-start">
                    <div>
                      <div className="font-medium">{transaction.tradeDetail}</div>
                      <div className="text-sm text-gray-500">
                        {formatDateTime(transaction.tradeTime)}
                      </div>
                      <div className="text-xs text-gray-400 mt-1">
                        {transaction.trade_partener_name}
                      </div>
                    </div>
                    <div className="text-right">
                      <div className={`font-semibold ${transaction.trade_amount > 0 ? "text-green-600" : "text-red-600"}`}>
                        {transaction.trade_amount > 0 ? "+" : ""}
                        {transaction.trade_amount.toLocaleString()}원
                      </div>
                      <div className="text-sm text-gray-500 mt-1">
                        잔액: {transaction.trade_balance.toLocaleString()}원
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))
          ) : (
            <div className="text-center py-8 text-gray-500">거래 내역이 없습니다.</div>
          )}
        </div>

        {/* 내보내기 버튼 */}
        <Button variant="outline" className="w-full">
          <Download className="h-4 w-4 mr-2" />
          거래 내역 내보내기
        </Button>
      </main>
    </>
  )
}

