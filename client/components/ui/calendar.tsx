"use client"

import type * as React from "react"
import { ChevronLeft, ChevronRight } from "lucide-react"
import { DayPicker } from "react-day-picker"
import { format } from "date-fns"
import { ko } from "date-fns/locale"

import { cn } from "@/lib/utils"
import { buttonVariants } from "@/components/ui/button"

export type CalendarProps = React.ComponentProps<typeof DayPicker> & {
  schedules?: Record<string, any[]>
  month?: Date
  onMonthChange?: (date: Date) => void
}

function Calendar({
  className,
  classNames,
  showOutsideDays = true,
  schedules = {},
  month,
  onMonthChange,
  ...props
}: CalendarProps) {
  return (
    <DayPicker
      showOutsideDays={showOutsideDays}
      month={month}
      onMonthChange={onMonthChange}
      locale={ko}
      className={cn("p-3", className)}
      classNames={{
        months: "flex flex-col space-y-4",
        month: "space-y-4",
        caption: "flex justify-center pt-1 relative items-center",
        caption_label: "text-sm font-medium",
        nav: "space-x-1 flex items-center",
        nav_button: cn(
          buttonVariants({ variant: "outline" }),
          "h-7 w-7 bg-transparent p-0 opacity-50 hover:opacity-100",
        ),
        nav_button_previous: "absolute left-1",
        nav_button_next: "absolute right-1",
        table: "w-full border-collapse space-y-1",
        head_row: "flex",
        head_cell: "text-muted-foreground rounded-md w-9 font-normal text-[0.8rem]",
        row: "flex w-full mt-2",
        cell: "h-9 w-9 text-center text-sm p-0 relative [&:has([aria-selected])]:bg-accent first:[&:has([aria-selected])]:rounded-l-md last:[&:has([aria-selected])]:rounded-r-md focus-within:relative focus-within:z-20",
        day: cn("h-9 w-9 p-0 font-normal aria-selected:opacity-100 hover:bg-muted rounded-md"),
        day_selected:
          "bg-primary text-primary-foreground hover:bg-primary hover:text-primary-foreground focus:bg-primary focus:text-primary-foreground",
        day_today: "border border-primary",
        day_outside: "text-muted-foreground opacity-50",
        day_disabled: "text-muted-foreground opacity-50",
        day_range_middle: "aria-selected:bg-accent aria-selected:text-accent-foreground",
        day_hidden: "invisible",
        ...classNames,
      }}
      components={{
        IconLeft: ({ ...props }) => <ChevronLeft className="h-4 w-4" />,
        IconRight: ({ ...props }) => <ChevronRight className="h-4 w-4" />,
        DayContent: ({ date, ...props }) => {
          const dateStr = format(date, "yyyy-MM-dd")
          const hasSchedule = schedules[dateStr] && schedules[dateStr].length > 0

          return (
            <div className="flex flex-col items-center justify-center w-full h-full">
              <span
                className={cn(
                  hasSchedule
                    ? "font-bold text-foreground" // 일정이 있는 경우 굵은 글씨와 기본 색상
                    : "text-gray-500", // 일정이 없는 경우 회색
                  props.activeModifiers?.selected && "text-primary-foreground", // 선택된 날짜
                  props.activeModifiers?.today && "text-primary font-bold", // 오늘 날짜
                )}
              >
                {date.getDate()}
              </span>
              {hasSchedule && <div className="w-1 h-1 bg-blue-500 rounded-full mt-0.5"></div>}
            </div>
          )
        },
      }}
      {...props}
    />
  )
}
Calendar.displayName = "Calendar"

export { Calendar }

