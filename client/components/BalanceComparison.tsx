import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"

interface BalanceComparisonProps {
  accountBalance: number
  monthlyTotal: number
}

export function BalanceComparison({ accountBalance, monthlyTotal }: BalanceComparisonProps) {
  const percentage = (monthlyTotal / accountBalance) * 100
  const isInsufficient = monthlyTotal > accountBalance

  return (
    <Card>
      <CardHeader>
        <CardTitle>잔액 비교</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-2">
          <div className="flex justify-between">
            <span>내 통장 잔액</span>
            <span className="font-semibold">{accountBalance.toLocaleString()}원</span>
          </div>
          <div className="flex justify-between">
            <span>이번 달 자동이체 총액</span>
            <span className="font-semibold">{monthlyTotal.toLocaleString()}원</span>
          </div>
          <Progress value={percentage} className="h-2" />
          <div className="text-sm text-right">
            {isInsufficient ? (
              <span className="text-red-500">
                잔액이 {(monthlyTotal - accountBalance).toLocaleString()}원 부족합니다
              </span>
            ) : (
              <span className="text-green-500">
                잔액이 {(accountBalance - monthlyTotal).toLocaleString()}원 남습니다
              </span>
            )}
          </div>
        </div>
      </CardContent>
    </Card>
  )
}

