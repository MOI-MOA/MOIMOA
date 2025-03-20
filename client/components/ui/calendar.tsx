"use client";

import { FC } from "react";
import { DayPicker, DayPickerProps } from "react-day-picker";
import "react-day-picker/dist/style.css";
import { format } from "date-fns";
import { ko } from "date-fns/locale";

interface Schedule {
  id: number;
  groupId: number;
  title: string;
  time: string;
  location: string;
  group: string;
  participants: number;
}

interface CustomCalendarProps {
  mode?: "single" | "multiple" | "range";
  selected?: Date;
  onSelect?: (date: Date | undefined) => void;
  className?: string;
  schedules?: Record<string, Schedule[]>;
  month?: Date;
  onMonthChange?: (month: Date) => void;
  fromMonth?: Date;
  toMonth?: Date;
}

/**
 * Calendar 컴포넌트
 * 1. 일정이 있는 날짜는 굵게 표시
 * 2. 달력이 가로로 꽉 차게 표시
 * 3. 현재 날짜에는 배경색으로 강조
 */
export const Calendar: FC<CustomCalendarProps> = ({
  schedules = {},
  selected,
  onSelect,
  mode = "single",
  month,
  onMonthChange,
  fromMonth,
  toMonth,
  className,
  ...props
}) => {
  const modifiers = {
    hasSchedule: (day: Date) => {
      const dateStr = format(day, "yyyy-MM-dd");
      return schedules[dateStr]?.length > 0;
    },
    today: (day: Date) => {
      const today = new Date();
      return format(day, "yyyy-MM-dd") === format(today, "yyyy-MM-dd");
    },
  };

  const modifiersClassNames = {
    hasSchedule: "font-bold text-blue-600",
    today: "bg-blue-50",
    selected: "bg-blue-100 text-blue-600",
  };

  return (
    <div className="w-full flex justify-center items-center">
        <DayPicker
          mode="single"
          selected={selected}
          onSelect={onSelect}
          month={month}
          onMonthChange={onMonthChange}
          fromMonth={fromMonth}
          toMonth={toMonth}
          locale={ko}
          modifiers={modifiers}
          modifiersClassNames={modifiersClassNames}
          classNames={{
            // root: "w-full",
            // months: "w-full flex justify-center",
            // month: "w-full",
            // table: "w-full border-collapse",
            // head: "w-full grid grid-cols-7",
            // head_cell: "h-12 flex items-center justify-center text-lg font-semibold",
            // row: "w-full grid grid-cols-7",
            // cell: "w-full flex-1 aspect-square flex justify-center items-center",
            day: "w-[100px] h-[50px]",
            day_button: "w-full text-center",
            // selected: "bg-blue-100 text-blue-600",
            // today: "bg-blue-50",
          }}
          {...props}
        />
    </div>
  );
};
