"use client"

import React, { use, useState, useEffect } from "react"
import { useRouter, useParams } from "next/navigation"
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
import { publicApi } from "@/lib/api"

// 기본 데이터 타입 정의
interface Transaction {
  tradeDetail: string
  tradeTime: string
  tradeAmount: number
  tradeBalance: number
}

interface AccountData {
  name: string
  accountNo: string
  accountBalance: number
  totalDeposit: number
  totalWithdrawal: number
  tradeList: Transaction[]
}

export default function AccountHistoryPage() {
  const { groupId } = useParams()
  const router = useRouter()
  const [searchTerm, setSearchTerm] = useState("")
  const [transactionType, setTransactionType] = useState("all")
  const [accountData, setAccountData] = useState<AccountData | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [limit, setLimit] = useState<number>(10)
  const [hasMore, setHasMore] = useState<boolean>(true)

  // API 데이터 가져오기
  const fetchAccountData = async (currentLimit: number) => {
    try {
      const requestData = {
        accountType: "PERSONAL",
        gatheringId: Number(groupId),
        limit: currentLimit
      }

      const response = await publicApi.post<AccountData>(
        "api/v1/trade/account-history",
        requestData
      ) as unknown as AccountData
      console.log(response)
      if (response) {
        const tradeList = response.tradeList || []
        if (tradeList.length < currentLimit) {
          setHasMore(false)
          toast({
            title: "알림",
            description: "거래내역의 전체를 불러왔습니다.",
          })
        }
        setAccountData(response)
      } else {
        setHasMore(false)
        setAccountData(null)
      }
    } catch (error) {
      console.error('계좌 내역을 불러오는데 실패했습니다:', error)
      toast({
        title: "데이터 로드 실패",
        description: "거래 내역을 불러오는데 실패했습니다.",
        variant: "destructive",
      })
      setAccountData(null)
      setHasMore(false)
    } finally {
      setIsLoading(false)
    }
  }

  // 초기 데이터 로딩
  useEffect(() => {
    fetchAccountData(limit)
  }, [groupId, limit])

  // 필터링된 거래 내역
  const filteredTransactions = accountData?.tradeList.filter((transaction) => {
    const matchesSearch =
      searchTerm === "" ||
      transaction.tradeDetail.toLowerCase().includes(searchTerm.toLowerCase())

    const matchesType =
      transactionType === "all" ||
      (transactionType === "deposit" && transaction.tradeAmount > 0) ||
      (transactionType === "withdrawal" && transaction.tradeAmount < 0)

    return matchesSearch && matchesType
  }) || []

  // 날짜/시간 포맷 함수
  const formatDateTime = (dateTimeString: string) => {
    const date = new Date(dateTimeString)
    return format(date, "yyyy년 MM월 dd일 HH:mm", { locale: ko })
  }

  // 더보기 버튼 클릭 핸들러 추가
  const handleLoadMore = () => {
    if (!hasMore) {
      toast({
        title: "알림",
        description: "거래내역의 전체를 불러왔습니다.",
      })
      return
    }
    setLimit(prev => prev + 10)
  }

  if (isLoading) {
    return <div className="flex items-center justify-center h-screen">로딩 중...</div>
  }

  if (!accountData) {
    return <div className="flex items-center justify-center h-screen">거래 내역이 없습니다.</div>
  }

  return (
    <>
      <Header title="모임통장 내역" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16">
        {/* 계좌 정보 */}
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-lg">{accountData.name}님 계좌</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-sm text-gray-500 mb-2">{accountData.accountNo}</p>
            <div className="text-2xl font-bold">{accountData.accountBalance.toLocaleString()}원</div>
            <div className="grid grid-cols-2 gap-4 mt-4">
              <div>
                <p className="text-sm text-gray-500">총 입금액</p>
                <p className="text-lg font-semibold text-green-600">+{accountData.totalDeposit.toLocaleString()}원</p>
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
            <>
              {filteredTransactions.map((transaction, index) => (
                <Card key={index} className="hover:shadow-sm transition-shadow">
                  <CardContent className="p-4">
                    <div className="flex justify-between items-start">
                      <div>
                        <div className="font-medium">{transaction.tradeDetail}</div>
                        <div className="text-sm text-gray-500">
                          {formatDateTime(transaction.tradeTime)}
                        </div>
                      </div>
                      <div className="text-right">
                        <div className={`font-semibold ${transaction.tradeAmount > 0 ? "text-green-600" : "text-red-600"}`}>
                          {transaction.tradeAmount > 0 ? "+" : ""}
                          {transaction.tradeAmount.toLocaleString()}원
                        </div>
                        <div className="text-sm text-gray-500 mt-1">
                          잔액: {transaction.tradeBalance.toLocaleString()}원
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </>
          ) : (
            <div className="text-center py-8 text-gray-500">거래 내역이 없습니다.</div>
          )}
        </div>

        {/* 거래내역 더보기 버튼 - 항상 표시 */}
        {filteredTransactions.length > 0 && (
          <Button 
            variant="outline" 
            className="w-full"
            onClick={handleLoadMore}
          >
            거래내역 더보기
          </Button>
        )}
      </main>
    </>
  )
}

