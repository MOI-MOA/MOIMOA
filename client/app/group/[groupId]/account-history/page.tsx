"use client"

import React, { use, useState, useEffect } from "react"
import { useRouter, useParams } from "next/navigation"
import { format } from "date-fns"
import { ko } from "date-fns/locale"
import { 
  CalendarIcon, 
  Download, 
  Search, 
  Wallet, 
  ArrowUpRight, 
  ArrowDownRight,
  RefreshCw,
  Filter,
  CreditCard,
  Clock,
  ChevronDown
} from "lucide-react"
import { Header } from "@/components/Header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Calendar } from "@/components/ui/calendar"
import axios from "axios"
import { toast } from "@/components/ui/use-toast"
import { publicApi, authApi } from "@/lib/api"

// 기본 데이터 타입 정의
interface Transaction {
  tradeName : string
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
        accountType: "GATHERING",
        gatheringId: Number(groupId),
        limit: currentLimit
      }

      const response = await authApi.post<AccountData>(
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
    return (
      <div className="flex items-center justify-center h-screen bg-slate-50">
        <div className="flex flex-col items-center space-y-4">
          <RefreshCw className="h-8 w-8 text-blue-600 animate-spin" />
          <p className="text-slate-600">거래 내역을 불러오는 중...</p>
        </div>
      </div>
    )
  }

  if (!accountData) {
    return (
      <div className="flex items-center justify-center h-screen bg-slate-50">
        <div className="flex flex-col items-center space-y-4">
          <CreditCard className="h-12 w-12 text-slate-400" />
          <p className="text-slate-600">거래 내역이 없습니다</p>
        </div>
      </div>
    )
  }

  return (
    <>
      <Header title="모임통장 내역" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16 bg-slate-50">
        <div className="max-w-lg mx-auto space-y-4">
          {/* 계좌 정보 */}
          <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
            <CardContent className="p-6">
              <div className="flex items-center justify-between mb-4">
                <div className="flex items-center space-x-3">
                  <div className="bg-blue-100 p-2 rounded-lg">
                    <Wallet className="h-6 w-6 text-blue-600" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-slate-800">{accountData.name} 모임통장</h3>
                    <p className="text-sm text-slate-500">{accountData.accountNo}</p>
                  </div>
                </div>
              </div>

              <div className="bg-gradient-to-br from-blue-50 to-indigo-50 p-4 rounded-xl mb-4">
                <p className="text-sm text-slate-600 mb-1">현재 잔액</p>
                <p className="text-2xl font-bold text-slate-800">
                  {accountData.accountBalance.toLocaleString()}원
                </p>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="bg-green-50 p-4 rounded-xl">
                  <div className="flex items-center space-x-2 mb-2">
                    <ArrowDownRight className="h-4 w-4 text-green-600" />
                    <span className="text-sm text-green-700">총 입금액</span>
                  </div>
                  <p className="text-lg font-semibold text-green-700">
                    +{accountData.totalDeposit.toLocaleString()}원
                  </p>
                </div>
                <div className="bg-red-50 p-4 rounded-xl">
                  <div className="flex items-center space-x-2 mb-2">
                    <ArrowUpRight className="h-4 w-4 text-red-600" />
                    <span className="text-sm text-red-700">총 출금액</span>
                  </div>
                  <p className="text-lg font-semibold text-red-700">
                    -{accountData.totalWithdrawal.toLocaleString()}원
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* 필터 섹션 */}
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-semibold text-slate-800 flex items-center">
                <Filter className="h-5 w-5 mr-2 text-blue-600" />
                거래 내역 필터
              </h2>
              <Button
                variant="outline"
                size="sm"
                onClick={() => {
                  setSearchTerm("")
                  setTransactionType("all")
                }}
                className="text-slate-600 hover:text-slate-700 rounded-lg border-slate-200"
              >
                초기화
              </Button>
            </div>

            <div className="flex space-x-2">
              <div className="relative flex-1">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-slate-400" />
                <Input
                  className="pl-9 py-5 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                  placeholder="거래내역 검색"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>

              <Select value={transactionType} onValueChange={setTransactionType}>
                <SelectTrigger className="w-[120px] rounded-xl border-slate-200">
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
                  <Card 
                    key={index} 
                    className="border-0 shadow-sm rounded-xl hover:shadow transition-shadow"
                  >
                    <CardContent className="p-4">
                      <div className="flex justify-between items-start">
                        <div className="flex items-start space-x-3">
                          <div className={`mt-1 p-2 rounded-lg ${
                            transaction.tradeAmount > 0 
                              ? "bg-green-50" 
                              : "bg-red-50"
                          }`}>
                            {transaction.tradeAmount > 0 
                              ? <ArrowDownRight className="h-4 w-4 text-green-600" />
                              : <ArrowUpRight className="h-4 w-4 text-red-600" />
                            }
                          </div>
                          <div>
                            <div className="font-medium text-slate-800">
                              {transaction.tradeDetail}
                            </div>
                            <div className="text-xs text-slate-400 mt-0.5">
                              {transaction.tradeName}
                            </div>
                            <div className="text-sm text-slate-500 flex items-center mt-1">
                              <Clock className="h-3.5 w-3.5 mr-1" />
                              {formatDateTime(transaction.tradeTime)}
                            </div>
                          </div>
                        </div>
                        <div className="text-right">
                          <div className={`font-semibold ${
                            transaction.tradeAmount > 0 
                              ? "text-green-600" 
                              : "text-red-600"
                          }`}>
                            {transaction.tradeAmount > 0 ? "+" : ""}
                            {transaction.tradeAmount.toLocaleString()}원
                          </div>
                          <div className="text-sm text-slate-500 mt-1">
                            잔액: {transaction.tradeBalance.toLocaleString()}원
                          </div>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </>
            ) : (
              <div className="text-center py-12">
                <CreditCard className="h-12 w-12 text-slate-400 mx-auto mb-3" />
                <p className="text-slate-600">검색 결과가 없습니다</p>
              </div>
            )}
          </div>

          {/* 거래내역 더보기 버튼 */}
          {filteredTransactions.length > 0 && hasMore && (
            <Button 
              variant="outline" 
              className="w-full rounded-xl border-slate-200 hover:bg-slate-100"
              onClick={handleLoadMore}
            >
              <div className="flex items-center justify-center">
                {isLoading ? (
                  <>
                    <RefreshCw className="h-4 w-4 mr-2 animate-spin" />
                    불러오는 중...
                  </>
                ) : (
                  <>
                    <ChevronDown className="h-4 w-4 mr-2" />
                    거래내역 더보기
                  </>
                )}
              </div>
            </Button>
          )}
        </div>
      </main>
    </>
  )
}

