defmodule Elixir_Intro do

    def fib(n) do
        cond do
            n == 0 ->
                0
            n < 3 ->
                1
            true ->
                fib(n-1) + fib(n-2)
        end
    end

    def area(shape, shape_info) do
        case shape do
            :rectangle ->
                {length, height} = shape_info
                length * height
            :triangle ->
                {base, height} = shape_info
                (base * height) / 2
            :circle ->
                radius = shape_info
                :math.pi * radius * radius
            :square ->
                side_length = shape_info
                side_length * side_length
            true ->
                IO.puts "invalid shape"
        end
    end

    def sqrList(nums) do
        for n <- nums do
            n * n
        end
    end

    def calcTotals(inventory) do
        for n <- inventory do
            {item, quantity, price} = n
            {item, quantity * price}
        end
    end

    def map(function, vals) do
        for n <- vals do
            function.(n)
        end
    end

    def quicksort([]) do [] end
    def quicksort(nums) do
        pivotIndex = :rand.uniform(length(nums))
        pivot = :lists.nth(pivotIndex, nums)
        rest = nums -- [pivot]
        smaller = for n <- rest, n < pivot do n end
        larger = for n <- rest, n >= pivot do n end
        quicksort(smaller) ++ [pivot] ++ quicksort(larger)
    end

    def quickSortServer() do
        receive do
            {nums, pid} ->
            sorted = quicksort(nums)
            send(pid, {sorted, self()})
        end
    end
end

defmodule Client do
    def callServer(pid,nums) do
        send(pid, {nums, self()})
	    listen()
    end

    def listen do
        receive do
            {sorted, _} -> sorted
	    end
    end
end
