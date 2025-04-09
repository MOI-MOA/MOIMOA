"use client"

import React, { useState, useEffect } from "react"
import { useRouter, useParams } from "next/navigation"
import { format } from "date-fns"
import { ko } from "date-fns/locale"
import { 
  Search, 
  RefreshCw, 
  ArrowDown, 
  ArrowUp, 
  Calendar, 
  CreditCard, 
  Wallet, 
  Filter, 
  RotateCw, 
  Clock,
  AlertCircle,
  Check
} from "lucide-react"
import { Header } from "@/components/Header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { toast } from "@/components/ui/use-toast"
import { authApi } from "@/lib/api"

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
        accountType: "PERSONAL",
        gatheringId: Number(groupId),
        limit: currentLimit
      }

      const response = await authApi.post<AccountData>(
        "api/v1/trade/account-history",
        requestData
      ) as unknown as AccountData
      
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
      <div className="flex flex-col items-center justify-center h-screen bg-slate-50">
        <RefreshCw className="h-8 w-8 text-blue-600 animate-spin mb-4" />
        <p className="text-slate-600">거래내역을 불러오는 중...</p>
      </div>
    )
  }

  if (!accountData) {
    return (
      <div className="flex flex-col items-center justify-center h-screen bg-slate-50">
        <AlertCircle className="h-12 w-12 text-slate-400 mb-4" />
        <p className="text-slate-600 font-medium">거래 내역이 없습니다</p>
      </div>
    )
  }

  return (
    <>
      <Header title="모임통장 내역" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-5 pb-16 bg-slate-50">
        {/* 계좌 정보 */}
        <h2 className="text-xl font-semibold text-slate-800 flex items-center">
          <CreditCard className="h-5 w-5 mr-2 text-blue-600" />
          계좌 정보
        </h2>
        
        <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
          <CardContent className="p-0">
            <div className="bg-gradient-to-r from-blue-600 to-indigo-600 p-6 text-white">
              <div className="flex items-center mb-2">
                <Wallet className="h-5 w-5 mr-2" />
                <h3 className="text-lg font-medium">{accountData.name}님의 계좌</h3>
              </div>
              <p className="text-sm text-blue-100 mb-3">{accountData.accountNo}</p>
              <p className="text-3xl font-bold">{accountData.accountBalance.toLocaleString()}원</p>
            </div>
            
            <div className="grid grid-cols-2 divide-x">
              <div className="p-4 text-center bg-white">
                <div className="flex items-center justify-center mb-1 text-green-600">
                  <ArrowDown className="h-4 w-4 mr-1" />
                  <p className="text-sm">총 입금액</p>
                </div>
                <p className="text-lg font-semibold text-green-600">
                  +{accountData.totalDeposit.toLocaleString()}원
                </p>
              </div>
              <div className="p-4 text-center bg-white">
                <div className="flex items-center justify-center mb-1 text-red-600">
                  <ArrowUp className="h-4 w-4 mr-1" />
                  <p className="text-sm">총 출금액</p>
                </div>
                <p className="text-lg font-semibold text-red-600">
                  -{accountData.totalWithdrawal.toLocaleString()}원
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* 필터 섹션 */}
        <h2 className="text-xl font-semibold text-slate-800 flex items-center mt-6">
          <Clock className="h-5 w-5 mr-2 text-blue-600" />
          거래 내역
        </h2>
        
        <Card className="border-0 shadow-sm rounded-xl">
          <CardContent className="p-4">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center">
                <Filter className="h-4 w-4 mr-2 text-slate-500" />
                <span className="text-sm font-medium text-slate-700">필터 옵션</span>
              </div>
              <Button 
                variant="outline" 
                size="sm" 
                className="rounded-lg border-slate-200 hover:bg-slate-100 text-slate-700"
                onClick={() => {
                  setSearchTerm("")
                  setTransactionType("all")
                }}
              >
                <RotateCw className="h-3.5 w-3.5 mr-1" />
                초기화
              </Button>
            </div>

            <div className="flex space-x-2">
              <div className="relative flex-1">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-slate-400" />
                <Input
                  className="pl-9 border-slate-200 rounded-lg focus:border-blue-400 focus:ring-blue-400"
                  placeholder="거래내용 검색"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>

              <Select value={transactionType} onValueChange={setTransactionType}>
                <SelectTrigger className="w-[120px] border-slate-200 rounded-lg focus:ring-blue-400">
                  <SelectValue placeholder="유형" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">전체</SelectItem>
                  <SelectItem value="deposit">입금</SelectItem>
                  <SelectItem value="withdrawal">출금</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </CardContent>
        </Card>

        {/* 거래 내역 목록 */}
        <div className="space-y-3 mt-2">
          {filteredTransactions.length > 0 ? (
            <>
              {filteredTransactions.map((transaction, index) => {
                const isPositive = transaction.tradeAmount > 0;
                const randomColor = [
                  "border-l-blue-500", 
                  "border-l-green-500",
                  "border-l-indigo-500",
                  "border-l-purple-500"
                ][index % 4];
                
                return (
                  <Card 
                    key={index} 
                    className={`hover:shadow-md transition-all duration-200 transform hover:-translate-y-0.5 border-0 shadow-sm rounded-xl border-l-4 ${randomColor}`}
                  >
                    <CardContent className="p-4">
                      <div className="flex justify-between items-start">
                        <div>
                          <div className="font-medium text-slate-800 mb-1">{transaction.tradeDetail}</div>
                          <div className="text-xs text-slate-400 mt-0.5">
                              {transaction.tradeName}
                          </div>
                          <div className="flex items-center text-sm text-slate-500">
                            <Calendar className="h-3.5 w-3.5 mr-1.5" />
                            {formatDateTime(transaction.tradeTime)}
                          </div>
                        </div>
                        <div className="text-right">
                          <div className={`font-semibold text-lg ${isPositive ? "text-green-600" : "text-red-600"}`}>
                            {isPositive ? "+" : ""}
                            {transaction.tradeAmount.toLocaleString()}원
                          </div>
                          <div className="text-sm text-slate-500 mt-1 bg-slate-100 px-2 py-0.5 rounded-full inline-block">
                            잔액: {transaction.tradeBalance.toLocaleString()}원
                          </div>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                );
              })}
              
              {/* 더보기 버튼 */}
              <Button 
                variant="outline" 
                className="w-full mt-3 rounded-xl shadow-sm border-slate-200 hover:bg-slate-100 text-slate-700 py-6"
                onClick={handleLoadMore}
                disabled={!hasMore}
              >
                {hasMore ? (
                  <>
                    <RotateCw className="h-4 w-4 mr-2" />
                    거래내역 더보기
                  </>
                ) : (
                  <>
                    <Check className="h-4 w-4 mr-2" />
                    모든 거래내역 불러옴
                  </>
                )}
              </Button>
            </>
          ) : (
            <div className="bg-white rounded-xl border-0 shadow-sm p-10 text-center">
              <div className="flex flex-col items-center">
                <AlertCircle className="h-10 w-10 text-slate-300 mb-3" />
                <p className="text-slate-600 font-medium">검색 결과가 없습니다</p>
                <p className="text-sm text-slate-500 mt-1">
                  다른 검색어를 입력하거나 필터를 초기화해보세요.
                </p>
                <Button 
                  variant="outline" 
                  size="sm" 
                  className="mt-4 rounded-lg border-slate-200"
                  onClick={() => {
                    setSearchTerm("")
                    setTransactionType("all")
                  }}
                >
                  <RotateCw className="h-3.5 w-3.5 mr-1.5" />
                  필터 초기화
                </Button>
              </div>
            </div>
          )}
        </div>
      </main>
    </>
  )
}

