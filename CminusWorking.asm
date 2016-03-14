	# All program code is placed after the
	# .text assembler directive
	.text

	# Declare main as a global function
	.globl	main

fun_add:
	# Registers used in this function: [$s1, $s0]
	# Store registers
	sw $s1, -20($sp)
	sw $s0, -16($sp)
	addi $sp, $sp, -20		#Entering parameter scope...
	lw $s0, 12($sp)		#Loading mutable.
	lw $s1, 8($sp)		#Loading mutable.
	add $s0, $s0, $s1
	sw $s0, 16($sp)
	addi $sp, $sp, 20		#Reverting parameter scope...
	# Restore registers
	lw $s1, -20($sp)
	lw $s0, -16($sp)
	jr $ra
main:
	# Registers used in this function: [$s2, $s1, $s0]
	# Store registers
	sw $s2, -12($sp)
	sw $s1, -8($sp)
	sw $s0, -4($sp)
	addi $sp, $sp, -16		#Entering parameter scope...
	li	$s0, 0
	sw $s0, -8($sp)		#Assigning to x.
	li	$s0, 1
	li $v0, 1
	move $a0, $s0
	syscall
	li $v0, 4
	la $a0, newline
	syscall
	lw $s0, -8($sp)		#Loading mutable.
	li $v0, 1
	move $a0, $s0
	syscall
	li $v0, 4
	la $a0, newline
	syscall
	li $v0, 4
	la $a0, label3
	syscall
	li $v0, 4
	la $a0, newline
	syscall
	li $s0, 1
	li $v0, 1
	move $a0, $s0
	syscall
	li $v0, 4
	la $a0, newline
	syscall
	li $s0, 2
	sw $s0, 12($sp)		#Assigning to a.
	li $s0, 3
	sw $s0, -4($sp)		#Assigning to b.
	lw $s0, 12($sp)		#Loading mutable.
	li $v0, 1
	move $a0, $s0
	syscall
	li $v0, 4
	la $a0, newline
	syscall
	lw $s0, -4($sp)		#Loading mutable.
	li $v0, 1
	move $a0, $s0
	syscall
	li $v0, 4
	la $a0, newline
	syscall
	addi $sp, $sp, -12		#Entering parameter scope...
	lw $s0, 20($sp)		#Loading mutable.
	lw $s1, 4($sp)		#Loading mutable.
	add $s0, $s0, $s1
	li $s1, 1
	add $s0, $s0, $s1
	li $s1, 2
	add $s0, $s0, $s1
	li $s1, 3
	add $s0, $s0, $s1
	li $s1, 4
	add $s0, $s0, $s1
	li $s1, 5
	add $s0, $s0, $s1
	li $s1, 6
	add $s0, $s0, $s1
	li $s1, 7
	add $s0, $s0, $s1
	li $s1, 8
	add $s0, $s0, $s1
	li $s1, 9
	add $s0, $s0, $s1
	li $s1, 10
	add $s0, $s0, $s1
	lw $s1, 20($sp)		#Loading mutable.
	sub $s0, $s0, $s1
	lw $s1, 4($sp)		#Loading mutable.
	sub $s0, $s0, $s1
	li $s1, 1
	sub $s0, $s0, $s1
	li $s1, 2
	sub $s0, $s0, $s1
	li $s1, 3
	sub $s0, $s0, $s1
	li $s1, 4
	sub $s0, $s0, $s1
	li $s1, 5
	sub $s0, $s0, $s1
	li $s1, 6
	sub $s0, $s0, $s1
	li $s1, 7
	sub $s0, $s0, $s1
	li $s1, 8
	sub $s0, $s0, $s1
	li $s1, 9
	sub $s0, $s0, $s1
	li $s1, 10
	sub $s0, $s0, $s1
	li $s1, 4
	add $s0, $s0, $s1
	sw $s0, -4($sp)		#Assigning to c.
	lw $s0, -4($sp)		#Loading mutable.
	li $v0, 1
	move $a0, $s0
	syscall
	li $v0, 4
	la $a0, newline
	syscall
	addi $sp, $sp, 12		#Reverting parameter scope...
	li $s0, 3
	li $s1, 1
	li $s2, 1
	add $s1, $s1, $s2
	add $s0, $s0, $s1
	li $v0, 1
	move $a0, $s0
	syscall
	li $v0, 4
	la $a0, newline
	syscall
	li $s0, 3
	li $s1, 4
	li $s2, 1
	sub $s1, $s1, $s2
	add $s0, $s0, $s1
	li $v0, 1
	move $a0, $s0
	syscall
	li $v0, 4
	la $a0, newline
	syscall
	li $s0, 3
	li $s1, 2
	li $s2, 2
	mult $s1, $s2
	mflo $s1
	add $s0, $s0, $s1
	li $v0, 1
	move $a0, $s0
	syscall
	li $v0, 4
	la $a0, newline
	syscall
	li $s0, 3
	li $s1, 10
	li $s2, 2
	div $s1, $s2
	mflo $s1
	add $s0, $s0, $s1
	li $v0, 1
	move $a0, $s0
	syscall
	li $v0, 4
	la $a0, newline
	syscall
	li $s0, 3
	li $s1, 16
	li $s2, 10
	div $s1, $s2
	mfhi $s1
	add $s0, $s0, $s1
	li $v0, 1
	move $a0, $s0
	syscall
	li $v0, 4
	la $a0, newline
	syscall
	li $s0, 1
	li $s1, 3
	li $s2, 2
	sw $s1, -8($sp)		#Setting parameter
	sw $s2, -12($sp)		#Setting parameter
	jal fun_add
	lw $s1, -4($sp)
	sw $s0, -8($sp)		#Setting parameter
	sw $s1, -12($sp)		#Setting parameter
	jal fun_add
	lw $s0, -4($sp)
	li $s1, 2
	add $s0, $s0, $s1
	li $s1, 1
	li $s2, 1
	sw $s1, -8($sp)		#Setting parameter
	sw $s2, -12($sp)		#Setting parameter
	jal fun_add
	lw $s1, -4($sp)
	add $s0, $s0, $s1
	li $v0, 1
	move $a0, $s0
	syscall
	li $v0, 4
	la $a0, newline
	syscall
	addi $sp, $sp, 16		#Reverting parameter scope...
	# Restore registers
	lw $s2, -12($sp)
	lw $s1, -8($sp)
	lw $s0, -4($sp)
	# exit
	li	$v0, 10
	syscall

	# All memory structures are placed after the
	# .data assembler directive
	.data

newline:	.asciiz "\n"
label3:	.asciiz	"This program should print the integers from 1 to n."
