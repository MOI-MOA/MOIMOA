"use client"

import { useSearchParams } from "next/navigation"

export default function MemberPage() {
  const searchParams = useSearchParams()
  const isManager = searchParams.get('isManager') === 'true'

  // isManager 값을 사용하여 페이지 렌더링
  // ...
} 