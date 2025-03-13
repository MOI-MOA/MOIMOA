"use client"

import { useState } from "react"
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

export default function AccountHistoryPage({ params }: { params: { groupId: string } }) {
  const router = useRouter()
  const groupId = params.groupId

  // 상태 관리
  const [searchTerm, setSearchTerm] = useState("")
  const [transactionType, setTransactionType] = useState("all")
  const [dateRange, setDateRange] = useState<{
    from: Date | undefined
    to: Date | undefined
  }>({
    from: undefined,
    to: undefined,
  })
  const [isCalendarOpen, setIsCalendarOpen] = useState(false)

  // 모임 정보 (실제로는 API에서 가져와야 함)
  const groupInfo = {
    name: "회사 동료",
    accountNumber: "신한은행 110-123-456789",
    balance: 2400000,
    totalDeposit: 3500000,
    totalWithdrawal: 1100000,
  }

  // 거래 내역 데이터 (실제로는 API에서 가져와야 함)
  const transactions = [
    {
      id: 1,
      date: "2024-03-25",
      time: "14:30",
      type: "deposit",
      description: "김철수 회비 입금",
      amount: 30000,
      balance: 2400000,
      member: "김철수",
    },
    {
      id: 2,
      date: "2024-03-20",
      time: "18:45",
      type: "deposit",
      description: "이영희 회비 입금",
      amount: 30000,
      balance: 2370000,
      member: "이영희",
    },
    {
      id: 3,
      date: "2024-03-15",
      time: "12:10",
      type: "withdrawal",
      description: "3월 정기 회식 결제",
      amount: 250000,
      balance: 2340000,
      member: "김철수",
    },
    {
      id: 4,
      date: "2024-03-10",
      time: "09:20",
      type: "deposit",
      description: "박지성 회비 입금",
      amount: 30000,
      balance: 2590000,
      member: "박지성",
    },
    {
      id: 5,
      date: "2024-03-05",
      time: "16:35",
      type: "deposit",
      description: "최민수 회비 입금",
      amount: 30000,
      balance: 2560000,
      member: "최민수",
    },
    {
      id: 6,
      date: "2024-02-28",
      time: "11:15",
      type: "withdrawal",
      description: "사무용품 구매",
      amount: 45000,
      balance: 2530000,
      member: "김철수",
    },
    {
      id: 7,
      date: "2024-02-25",
      time: "10:00",
      type: "deposit",
      description: "정다운 회비 입금",
      amount: 30000,
      balance: 2575000,
      member: "정다운",
    },
    {
      id: 8,
      date: "2024-02-20",
      time: "15:40",
      type: "withdrawal",
      description: "2월 정기 회식 결제",
      amount: 200000,
      balance: 2545000,
      member: "김철수",
    },
  ]

  // 필터링된 거래 내역
  const filteredTransactions = transactions.filter((transaction) => {
    // 검색어 필터링
    const matchesSearch =
      searchTerm === "" ||
      transaction.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
      transaction.member.toLowerCase().includes(searchTerm.toLowerCase())

    // 거래 유형 필터링
    const matchesType = transactionType === "all" || transaction.type === transactionType

    // 날짜 범위 필터링
    let matchesDateRange = true
    if (dateRange.from) {
      const transactionDate = new Date(transaction.date)
      matchesDateRange = transactionDate >= dateRange.from

      if (dateRange.to) {
        matchesDateRange = matchesDateRange && transactionDate <= dateRange.to
      }
    }

    return matchesSearch && matchesType && matchesDateRange
  })

  // 날짜 포맷 함수
  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    return format(date, "yyyy년 MM월 dd일", { locale: ko })
  }

  // 필터 초기화 함수
  const resetFilters = () => {
    setSearchTerm("")
    setTransactionType("all")
    setDateRange({ from: undefined, to: undefined })
  }

  return (
    <>
      <Header title="모임통장 내역" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16">
        {/* 계좌 정보 */}
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-lg">{groupInfo.name} 모임통장</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-sm text-gray-500 mb-2">{groupInfo.accountNumber}</p>
            <div className="text-2xl font-bold">{groupInfo.balance.toLocaleString()}원</div>
            <div className="grid grid-cols-2 gap-4 mt-4">
              <div>
                <p className="text-sm text-gray-500">총 입금액</p>
                <p className="text-lg font-semibold text-green-600">+{groupInfo.totalDeposit.toLocaleString()}원</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">총 출금액</p>
                <p className="text-lg font-semibold text-red-600">-{groupInfo.totalWithdrawal.toLocaleString()}원</p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* 필터 섹션 */}
        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold">거래 내역</h2>
            <Button variant="outline" size="sm" onClick={resetFilters}>
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

            <Popover open={isCalendarOpen} onOpenChange={setIsCalendarOpen}>
              <PopoverTrigger asChild>
                <Button variant="outline" className="w-[150px] justify-start text-left font-normal">
                  <CalendarIcon className="mr-2 h-4 w-4" />
                  {dateRange.from ? (
                    dateRange.to ? (
                      <>
                        {format(dateRange.from, "MM.dd")} - {format(dateRange.to, "MM.dd")}
                      </>
                    ) : (
                      format(dateRange.from, "yyyy.MM.dd")
                    )
                  ) : (
                    <span>기간 선택</span>
                  )}
                </Button>
              </PopoverTrigger>
              <PopoverContent className="w-auto p-0" align="start">
                <Calendar
                  initialFocus
                  mode="range"
                  defaultMonth={dateRange.from}
                  selected={dateRange}
                  onSelect={(range) => {
                    setDateRange(range)
                    if (range.to) {
                      setIsCalendarOpen(false)
                    }
                  }}
                  numberOfMonths={2}
                />
              </PopoverContent>
            </Popover>
          </div>
        </div>

        {/* 거래 내역 목록 */}
        <div className="space-y-3">
          {filteredTransactions.length > 0 ? (
            filteredTransactions.map((transaction) => (
              <Card key={transaction.id} className="hover:shadow-sm transition-shadow">
                <CardContent className="p-4">
                  <div className="flex justify-between items-start">
                    <div>
                      <div className="font-medium">{transaction.description}</div>
                      <div className="text-sm text-gray-500">
                        {formatDate(transaction.date)} {transaction.time}
                      </div>
                      <div className="text-xs text-gray-400 mt-1">처리자: {transaction.member}</div>
                    </div>
                    <div className="text-right">
                      <div
                        className={`font-semibold ${
                          transaction.type === "deposit" ? "text-green-600" : "text-red-600"
                        }`}
                      >
                        {transaction.type === "deposit" ? "+" : "-"}
                        {transaction.amount.toLocaleString()}원
                      </div>
                      <div className="text-sm text-gray-500 mt-1">잔액: {transaction.balance.toLocaleString()}원</div>
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

