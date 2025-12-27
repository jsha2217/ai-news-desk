const LoadingSpinner = ({ size = 'large' }) => {
  const sizeClasses = {
    small: 'h-6 w-6',
    medium: 'h-8 w-8',
    large: 'h-12 w-12'
  }

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className={`animate-spin rounded-full border-t-2 border-b-2 border-cyan-400 ${sizeClasses[size]}`}></div>
    </div>
  )
}

export default LoadingSpinner
